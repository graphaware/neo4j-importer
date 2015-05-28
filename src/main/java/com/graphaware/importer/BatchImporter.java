package com.graphaware.importer;

import com.graphaware.importer.cache.Caches;
import com.graphaware.importer.cache.MapDBCaches;
import com.graphaware.importer.cli.CommandLineParser;
import com.graphaware.importer.config.ImportConfig;
import com.graphaware.importer.context.ImportContext;
import com.graphaware.importer.context.SimpleImportContext;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.location.DataLocator;
import com.graphaware.importer.data.location.FileLocator;
import com.graphaware.importer.importer.Importer;
import com.graphaware.importer.plan.DefaultExecutionPlan;
import com.graphaware.importer.plan.ExecutionPlan;
import com.graphaware.importer.stats.LoggingStatisticsCollector;
import com.graphaware.importer.stats.StatisticsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Abstract base class for batch importers. In order to implement one, extend this class, implement at least the mandatory
 * methods, and create a main method that will call onto {@link #run(String[])}, like this:
 * <p/>
 * <p/>
 * <code>
 * public static void main(String[] args) {
 * new MyBatchImporter().run(args);
 * }
 * </code>
 *
 * @param <T> type of the import config used by this importer.
 */
public abstract class BatchImporter<T extends ImportConfig> {

    private static final Logger LOG = LoggerFactory.getLogger(BatchImporter.class);

    private final Object monitor = new Object();

    /**
     * Run this importer.
     *
     * @param args command-line arguments.
     */
    public void run(String[] args) {
        try {
            T config = commandLineParser().parseArgs(args);
            if (config == null) {
                return;
            }

            ImportContext context = createContext(config);

            context.bootstrap();
            context.check();

            StatisticsCollector stats = createStats(context);
            stats.startTiming();

            performImport(context);

            stats.printTiming();

            LOG.info("IMPORT SUCCESSFUL");
        } catch (Throwable throwable) {
            LOG.error("An exception occurred: ", throwable);
            LOG.info("IMPORT FAILED");
        }
    }

    /**
     * Create a command-line parser.
     *
     * @return parser.
     */
    protected abstract CommandLineParser<T> commandLineParser();

    /**
     * Create an import context.
     *
     * @param config configurations built from command-line arguments.
     * @return context.
     */
    protected ImportContext createContext(T config) {
        return new SimpleImportContext(config, createCaches(), createInputDataLocator(config), createOutputDataLocator(config));
    }

    /**
     * Create caches used throughout the import.
     *
     * @return caches.
     */
    protected Caches createCaches() {
        return new MapDBCaches();
    }

    /**
     * Create a locator for import input data.
     *
     * @param config import config.
     * @return data locator.
     */
    protected abstract DataLocator createInputDataLocator(T config);

    /**
     * Create a locator for import output data.
     *
     * @param config import config.
     * @return data locator.
     */
    protected DataLocator createOutputDataLocator(T config) {
        return new FileLocator(config.getOutputDir(), output());
    }

    /**
     * Create a mapping between output data and their logical name. Returns an empty map by default.
     *
     * @return output mapping.
     */
    protected Map<Data, String> output() {
        return Collections.emptyMap();
    }

    /**
     * Create a statistics collector for the entire import.
     *
     * @param context context.
     * @return stats collector.
     */
    protected StatisticsCollector createStats(ImportContext context) {
        return new LoggingStatisticsCollector("IMPORT");
    }

    /**
     * Perform the actual import.
     *
     * @param context import context.
     */
    private void performImport(ImportContext context) {
        LOG.info("Starting import...");

        Set<Importer> importers = createImporters();
        ExecutionPlan plan = new DefaultExecutionPlan(importers, context);
        performImport(context, plan);

        LOG.info("Finishing import...");

        context.shutdown();

        LOG.info("Batch inserter shut down.");
    }

    /**
     * Create a set of importers that will perform the import.
     *
     * @return importers.
     */
    protected abstract Set<Importer> createImporters();

    /**
     * Perform a multi-threaded import.
     *
     * @param context       import context.
     * @param executionPlan import execution plan.
     */
    private void performImport(ImportContext context, final ExecutionPlan executionPlan) {
        for (Importer importer : executionPlan.getOrderedImporters()) {
            LOG.info("Preparing " + importer.name());
            importer.prepare(context);
        }

        for (final Importer importer : executionPlan.getOrderedImporters()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!executionPlan.canRun(importer)) {
                        synchronized (monitor) {
                            try {
                                monitor.wait();
                            } catch (InterruptedException e) {
                                //ok
                            }
                        }
                    }

                    try {
                        importer.performImport();
                    } finally {
                        synchronized (monitor) {
                            executionPlan.clearCaches();
                            monitor.notifyAll();
                        }
                    }
                }
            }, "IMPORTER - " + importer.name()).start();
        }

        while (!executionPlan.allFinished()) {
            synchronized (monitor) {
                try {
                    monitor.wait(10000);
                } catch (InterruptedException e) {
                    //ok
                }
            }
        }

        LOG.info("Import finished, will destroy caches.");

        context.caches().destroy();

        LOG.info("Will create indices.");

        for (Importer batchImporter : executionPlan.getOrderedImporters()) {
            batchImporter.createIndices();
        }

        LOG.info("Indices created.");
    }
}
