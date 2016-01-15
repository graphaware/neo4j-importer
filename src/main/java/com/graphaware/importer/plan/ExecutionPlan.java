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

package com.graphaware.importer.plan;

import com.graphaware.importer.importer.Importer;

import java.util.List;

/**
 * Import execution plan. Based on a set of {@link com.graphaware.importer.importer.Importer}s, it should create the
 * best plan to execute them. For best performance, import is best done on multiple cores in multiple threads. With this
 * in mind, {@link #canRun(com.graphaware.importer.importer.Importer)} method can be called at any time to determine, whether
 * a given {@link com.graphaware.importer.importer.Importer} can be run, i.e. if all the
 * {@link com.graphaware.importer.importer.Importer}s it depends on have already finished.
 * <p/>
 * Implementations must be thread-safe.
 */
public interface ExecutionPlan {

    /**
     * Get the importers ordered such that if any importer A depends on importer B (usually by means of using a {@link com.graphaware.importer.cache.Cache}
     * created by B), the A will appear in the list after B. This is useful for operations that need to be carried out
     * sequentially on each importer, such as creating and injecting {@link com.graphaware.importer.cache.Cache}s, or
     * to get a predictable order of actions that need be single-threaded, such as creating indices.
     *
     * @return a list of importers.
     */
    List<Importer> getOrderedImporters();

    /**
     * Can the given importer be run?
     *
     * @param importer to check.
     * @return <code>true</code> iff all importers that it depends on (if any) have finished.
     */
    boolean canRun(Importer importer);

    /**
     * Clear all {@link com.graphaware.importer.cache.Caches} that will not be needed any more.
     */
    void clearCaches();

    /**
     * @return <code>true</code> iff all {@link com.graphaware.importer.importer.Importer}s have finished.
     */
    boolean allFinished();
}
