/*
 * Copyright (c) 2015 GraphAware
 *
 * This file is part of GraphAware Framework.
 *
 * GraphAware Framework is free software: you can redistribute it and/or modify it under the terms
 *  of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of the
 * GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.graphaware.importer.importer;

import com.graphaware.importer.cache.Caches;
import com.graphaware.importer.context.ImportContext;
import com.graphaware.importer.data.access.DataReader;
import com.graphaware.importer.stats.StatisticsCollector;
import com.graphaware.importer.util.BlockingArrayBlockingQueue;
import org.neo4j.graphdb.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link BaseImporter} working with objects (DTOs) for data encapsulation.
 * <p/>
 * DTOs are created by synchronously reading a records from {@link com.graphaware.importer.data.access.DataReader}.
 * Created DTOs are then submitted to an {@link java.util.concurrent.ExecutorService}, running in a separate thread,
 * responsible for doing any processing on the DTOs and ultimately creating nodes and relationships out of it.
 *
 * @param <T> type of the DTO this inserter works with.
 */
public abstract class BaseImporter<T> implements Importer {

    private static final String IMPORT_STATS = "Import Statistics";

    private static final Logger LOG = LoggerFactory.getLogger(BaseImporter.class);

    protected ImportContext context;

    protected StatisticsCollector collector;

    private volatile State state = State.NOT_STARTED;

    private final ExecutorService executor;

    /**
     * Construct a new importer.
     */
    public BaseImporter() {
        executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new BlockingArrayBlockingQueue<Runnable>(queueCapacity()), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link #inputData()#name()} by default.
     */
    @Override
    public String name() {
        return inputData().name();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void prepare(ImportContext importContext) {
        this.context = importContext;

        for (String cacheName : context.caches().createdCaches(this)) {
            createCache(context.caches(), cacheName);
        }
        context.caches().inject(this);

        collector = context.createStatistics(name());
    }

    /**
     * Populate database. {@link #prepare(com.graphaware.importer.context.ImportContext)} is guaranteed to be called before this method.
     */
    @Override
    public final void performImport() {
        if (context == null) {
            throw new IllegalStateException("Context has not been provided to inserter. Please call the prepare method before populating database");
        }

        synchronized (this) {
            if (getState() != State.NOT_STARTED) {
                throw new IllegalStateException(name() + " asked to perform import, but has already been started!");
            }
            setState(State.RUNNING);
        }

        try {
            DataReader reader = context.createReader(inputData());

            if (reader == null) {
                LOG.warn("Could not create reader for " + inputData());
                return;
            }

            LOG.info("Populating " + inputData() + "...");

            while (reader.readRecord()) {
                if (reader.getRow() % loggingInterval() == 0) {
                    LOG.info("Imported " + reader.getRow() + " records. (" + name() + ")");
                }

                processSingleRow(reader);
            }

            reader.close();

            executor.shutdown();
            try {
                executor.awaitTermination(30, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                LOG.warn("Could not finish processing the queue of DTOs in 30 minutes!");
            }

            this.shutdown();

            collector.printStats();
        } finally {
            setState(State.FINISHED);
        }
    }

    /**
     * Create indices. No-op by default, to be overridden by subclasses.
     */
    @Override
    public void createIndices() {
        //no-op, to be overridden.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized State getState() {
        return state;
    }

    private synchronized void setState(State state) {
        this.state = state;
    }

    /**
     * Get the capacity of the DTO queue. Defaults to 100,000.
     *
     * @return queue capacity.
     */
    protected int queueCapacity() {
        return 100_000;
    }

    /**
     * How often should a message be logged about the progress?
     *
     * @return number of records, defaults to 100,000.
     */
    protected int loggingInterval() {
        return 100_000;
    }

    private void processSingleRow(DataReader reader) {
        final int row = reader.getRow();
        final String rawData = reader.getRawRecord();

        //Produce objects in the same thread.

        T object;
        try {
            collector.incrementsStats(IMPORT_STATS, "Processed");
            object = produceObject(reader);

            if (object == null) {
                collector.incrementsStats(IMPORT_STATS, "No Object Produced");
                return;
            }

        } catch (RuntimeException e) {
            collector.incrementsStats(IMPORT_STATS, "Skipped");
            collector.incrementsStats("Production", e.getMessage());
            LOG.warn("Failed to produce object. Skipping row " + row + ": " + e.getMessage() + ". Raw data: " + rawData, e);
            return;
        }

        //Pre-process (normalize, validate,...) and insert to the database.

        final T o = object;

        executor.submit(new Runnable() {
            @Override
            public void run() {
                if (preProcess(o, row, rawData)) {
                    return;
                }

                try {
                    processObject(o);
                    collector.incrementsStats(IMPORT_STATS, "Inserted");
                } catch (RuntimeException e) {
                    collector.incrementsStats(IMPORT_STATS, "Skipped");
                    collector.incrementsStats("Insertion", e.getMessage());
                    LOG.warn("Failed to insert object. Skipping row " + row + ": " + e.getMessage() + ". Raw data: " + rawData, e);
                }
            }
        });
    }

    /**
     * Pre-process the object. This can include normalization, validation, etc.
     *
     * @param object  to pre-process.
     * @param row     row number.
     * @param rawData raw data used to produce this object.
     * @return whether to prevent this object's insertion, i.e. <code>true</code> iff this object should not be processed. <code>false</code> by default.
     */
    protected boolean preProcess(T object, int row, String rawData) {
        return false;
    }

    /**
     * Produce the object (DTO) from the data record.
     *
     * @param record row.
     * @return the object, can be <code>null</code> if it can't be produced for whatever reason.
     */
    public abstract T produceObject(DataReader record);

    /**
     * Process an object, i.e. create nodes and relationships out of it. The object is guaranteed not to be <code>null</code>.
     *
     * @param object to process.
     */
    public abstract void processObject(T object);

    /**
     * Convenience method for subclasses to create indices.
     *
     * @param label      for which to create index.
     * @param properties to index for the given label.
     */
    protected final void createIndex(Label label, String... properties) {
        for (String property : properties) {
            LOG.info("Creating index for label " + label.name() + " and property " + property);
            context.inserter().createDeferredSchemaIndex(label).on(property).create();
        }
    }

    /**
     * Let this importer create a cache. This method is guaranteed to only be called for caches annotated with {@link com.graphaware.importer.cache.InjectCache}
     * where {@link com.graphaware.importer.cache.InjectCache#creator()} equals <code>true</code>.
     *
     * @param caches in which to create a new cache.
     * @param name   name of the new cache to be created.
     * @throws java.lang.IllegalArgumentException if this importer doesn't know how to create the cache.
     */
    protected void createCache(Caches caches, String name) {
        throwCreateCacheException(name);
    }

    /**
     * Throw an exception about not being able to create a cache.
     *
     * @param name of the cache that can't be created.
     * @throws java.lang.IllegalArgumentException always.
     */
    protected final void throwCreateCacheException(String name) {
        throw new IllegalArgumentException("Importer " + name() + " has been asked to create " + name + ". Please override the createCache(..) method and create the cache!");
    }

    /**
     * Shutdown this importer. No-op by default, to be overridden by subclasses withing to close resources etc. before
     * finishing import.
     */
    protected void shutdown() {

    }
}
