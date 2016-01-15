/*
 * Copyright (c) 2013-2016 GraphAware
 *
 * This file is part of the GraphAware Framework.
 *
 * GraphAware Framework is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of the
 * GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

