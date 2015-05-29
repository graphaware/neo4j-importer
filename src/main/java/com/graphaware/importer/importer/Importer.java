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

import com.graphaware.importer.context.ImportContext;

/**
 * A component responsible for importing a single concern. Typically, a "concern" is a single file or SQL query.
 * Implementations must be thread-safe.
 */
public interface Importer {

    /**
     * Importer state.
     */
    public enum State {
        NOT_STARTED,
        RUNNING,
        FINISHED
    }

    /**
     * Get the name of this importer. Useful for logging, thread naming, etc.
     *
     * @return importer name.
     */
    String name();

    /**
     * Prepare this importer for populating the database. Guaranteed to be called before {@link #performImport()}.
     *
     * @param importContext context.
     */
    void prepare(ImportContext importContext);

    /**
     * Perform the actual import, i.e., create nodes and relationships.
     */
    void performImport();

    /**
     * Create indices (and constraints).
     */
    void createIndices();

    /**
     * Get the current state of this importer.
     *
     * @return state.
     */
    State getState();
}
