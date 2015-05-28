package com.graphaware.importer.stats;

/**
 * A component collecting statistics about import.
 */
public interface StatisticsCollector {

    /**
     * Start timing of the import.
     */
    void startTiming();

    /**
     * Print time elapsed so far..
     */
    void printTiming();

    /**
     * Increment a statistic by 1.
     *
     * @param category category of the statistic, e.g. "errors", "warnings", "validation problems", etc.
     * @param name     name of the statistic, e.g. "missing property", ...
     */
    void incrementsStats(String category, String name);

    /**
     * Increment a statistic by a number.
     *
     * @param category category of the statistic, e.g. "errors", "warnings", "validation problems", etc.
     * @param name     name of the statistic, e.g. "missing property", ...
     * @param number   to increment by.
     */
    void setStats(String category, String name, int number);

    /**
     * Print all the stats collected so far.
     */
    void printStats();
}

