/*
 * Copyright (c) 2013-2015 GraphAware
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

package com.graphaware.importer.data.access;

/**
 * A reader of data. Does not have to be (and usually isn't) thread-safe. Therefore, this class should be accessed
 * in a single thread.
 *
 * @param <O> type of object that is retrieved from cells.
 */
public interface DataReader<O> extends DataAccess {

    /**
     * Initialize the reader.
     */
    void initialize();

    /**
     * Open the reader.
     *
     * @param connectionString identification of the data source (e.g. SQL query, file name, etc.)
     * @param hint             human-readable representation of the connection for naming threads, logging, etc.
     */
    void read(String connectionString, String hint);

    /**
     * Close the reader and release all resources.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    void close();

    /**
     * Read an object from a column (family,...).
     *
     * @param columnName name of the column.
     * @return an object.
     */
    O readObject(String columnName);

    /**
     * Get current row number.
     *
     * @return current row number.
     */
    int getRow();

    /**
     * Read the next record, i.e. advance the cursor by 1.
     *
     * @return true iff there was a next record.
     */
    boolean readRecord();

    /**
     * Get the current record in its raw form.
     *
     * @return raw record.
     */
    String getRawRecord();
}
