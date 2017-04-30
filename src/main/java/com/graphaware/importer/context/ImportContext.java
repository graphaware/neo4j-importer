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

package com.graphaware.importer.context;

import com.graphaware.importer.cache.Caches;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.access.DataReader;
import com.graphaware.importer.stats.StatisticsCollector;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;

/**
 * Context for an import.
 */
public interface ImportContext {

    /**
     * Perform essential bootstrap of the context, i.e., create {@link org.neo4j.unsafe.batchinsert.BatchInserter} and
     * {@link org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider}.
     */
    void essentialBootstrap();

    /**
     * Bootstrap the context, i.e. prepare for import.
     */
    void fullBootstrap();

    /**
     * Check that the context is OK.
     *
     * @throws java.lang.IllegalStateException if not OK.
     */
    void check();

    /**
     * Create a new statistics collector for stats with the given name.
     *
     * @param name stats name. Must not be <code>null</code> or empty.
     * @return collector.
     */
    StatisticsCollector createStatistics(String name);

    /**
     * Get caches used throughout the import.
     *
     * @return caches.
     */
    Caches caches();

    /**
     * Get the batch inserter associated with this import.
     *
     * @return batch inserter.
     */
    BatchInserter inserter();

    /**
     * Get the index provider associated with this import.
     *
     * @return batch inserter index provider.
     */
    BatchInserterIndexProvider indexProvider();

    /**
     * Create readers for the given data.
     *
     * @param data for which to create readers. Must not be <code>null</code>.
     * @return data readers. Null if no data reader for given input data can be created.
     */
    DataReader[] createReaders(Data data);

    /**
     * Shutdown the context after import has finished.
     */
    void shutdown();
}
