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
 * A {@link DataReader} of tabular data. Does not have to be (and usually isn't) thread-safe. Therefore, this class should be accessed
 * in a single thread.
 */
public interface TabularDataReader extends DataReader<String> {

    /**
     * Read a long number from a column.
     *
     * @param columnName name of the column.
     * @return a long number, <code>null</code> if it is empty or not a long.
     */
    Long readLong(String columnName);

    /**
     * Read an int number from a column.
     *
     * @param columnName name of the column.
     * @return a int number, <code>null</code> if it is empty or not an int.
     */
    Integer readInt(String columnName);

    /**
     * Read a long representation of a date from a column.
     *
     * @param columnName name of the column.
     * @return a date as ms since epoch, <code>null</code> if it is empty or not a date.
     */
    Long readDate(String columnName);
}
