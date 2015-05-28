package com.graphaware.importer.stats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default production implementation of {@link StatisticsCollector}.
 */
public class LoggingStatisticsCollector implements StatisticsCollector {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingStatisticsCollector.class);
    public static final String DIVIDER = "=========================================";

    private final StopWatch sw = new StopWatch();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> counters = new ConcurrentHashMap<>();
    private final String name;

    /**
     * Create a new statistics collector.
     *
     * @param name name of the stats, must not be <code>null</code> or empty.
     */
    public LoggingStatisticsCollector(String name) {
        Assert.hasLength(name);

        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startTiming() {
        sw.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printTiming() {
        LOG.info(name + " took " + sw.getElapsedTimeSecs() + " seconds / " + Math.round(sw.getElapsedTimeSecs() / 60.0) + " minutes");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void incrementsStats(String category, String name) {
        getCounter(category, name).incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStats(String category, String name, int number) {
        getCounter(category, name).set(number);
    }

    protected ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> getCounters() {
        return counters;
    }

    private AtomicInteger getCounter(String category, String name) {
        if (name == null) {
            name = "null";
        }

        ConcurrentHashMap<String, AtomicInteger> counter = counters.get(category);

        if (counter == null) {
            counters.putIfAbsent(category, new ConcurrentHashMap<String, AtomicInteger>());
            counter = counters.get(category);
        }

        AtomicInteger count = counter.get(name);

        if (count == null) {
            counter.putIfAbsent(name, new AtomicInteger(0));
            count = counter.get(name);
        }

        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printStats() {
        for (String category : counters.keySet()) {
            LOG.info(DIVIDER);
            LOG.info(name + ": " + category);
            LOG.info(DIVIDER);

            printCategory(category);

            LOG.info(DIVIDER);
        }
    }

    /**
     * Print (log) all the stats for a given category.
     *
     * @param category to print.
     */
    protected void printCategory(String category) {
        Map<String, AtomicInteger> stats = counters.get(category);
        for (String statistic : stats.keySet()) {
            StringBuilder spaces = new StringBuilder();
            AtomicInteger value = stats.get(statistic);
            int length = statistic.length() + value.toString().length();
            while (length + spaces.length() < DIVIDER.length() - 1) {
                spaces.append(" ");
            }
            LOG.info(statistic + ":" + spaces + value.get());
        }
    }
}
