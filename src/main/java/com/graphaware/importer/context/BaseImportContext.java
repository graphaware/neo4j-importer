/*
 * Copyright (c) 2014 GraphAware
 *
 * This file is part of GraphAware.
 *
 * GraphAware is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of
 * the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.graphaware.importer.context;

import com.graphaware.importer.config.ImportConfig;
import com.graphaware.importer.inserter.SynchronizedBatchInserter;
import com.graphaware.importer.stats.LoggingStatisticsCollector;
import com.graphaware.importer.stats.StatisticsCollector;
import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Bare-bones base-class for {@link ImportContext} implementations.
 */
abstract class BaseImportContext implements ImportContext {

    private static final Logger LOG = LoggerFactory.getLogger(BaseImportContext.class);

    protected final ImportConfig config;

    private BatchInserter inserter;
    private BatchInserterIndexProvider indexProvider;

    /**
     * Create a new import context with the given config.
     *
     * @param config import config. Must not be <code>null</code>.
     */
    public BaseImportContext(ImportConfig config) {
        Assert.notNull(config);

        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final BatchInserter inserter() {
        return inserter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final BatchInserterIndexProvider indexProvider() {
        return indexProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void essentialBootstrap() {
        inserter = createBatchInserter();
        indexProvider = createIndexProvider();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void fullBootstrap() {
        preBootstrap();

        essentialBootstrap();

        postBootstrap();
    }

    /**
     * Perform preliminary actions before context bootstrapping. No-op by default, intended to be overridden.
     */
    protected void preBootstrap() {
    }

    /**
     * Perform additional context bootstrapping. No-op by default, intended to be overridden.
     */
    protected void postBootstrap() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void check() {
        doCheck();
    }

    /**
     * Perform additional checks. No-op by default, intended to be overridden.
     *
     * @throws java.lang.IllegalStateException if not OK.
     */
    protected void doCheck() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatisticsCollector createStatistics(String name) {
        return new LoggingStatisticsCollector(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void shutdown() {
        preShutdown();
        indexProvider().shutdown();
        inserter().shutdown();
        postShutdown();
    }

    /**
     * Perform actions before the context is shutdown. No-op by default, intended to be overridden.
     */
    protected void preShutdown() {
    }

    /**
     * Perform actions after the context has been shutdown. No-op by default, intended to be overridden.
     * <p/>
     * Note that by this time, you should only perform logging and the like, everything else in the context (especially inserters and index providers) has been shut down!
     */
    protected void postShutdown() {
    }

    /**
     * Create a {@link org.neo4j.unsafe.batchinsert.BatchInserter}.
     *
     * @return batch inserter.
     */
    protected final BatchInserter createBatchInserter() {
        return new SynchronizedBatchInserter(BatchInserters.inserter(config.getGraphDir(), new HashMap<String, String>((Map) getProperties())));
    }

    /**
     * Get the properties passed in to the batch inserter.
     *
     * @return properties.
     */
    protected Properties getProperties() {
        final Properties properties = new Properties();
        try {
            properties.load(new ClassPathResource(config.getProps()).getInputStream());
        } catch (IOException e) {
            LOG.warn("Could not read properties");
            throw new RuntimeException(e);
        }
        return properties;
    }

    /**
     * Create a {@link org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider}.
     *
     * @return index provider.
     */
    protected final BatchInserterIndexProvider createIndexProvider() {
        return new LuceneBatchInserterIndexProvider(((SynchronizedBatchInserter) inserter).getBatchInserter());
    }
}
