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

package com.graphaware.importer.data.access;

import java.util.Map;

/**
 * A component that maps a {@link com.graphaware.importer.cache.Cache} entry into a Java object as if it was read from
 * a tabular source.
 */
public interface CacheEntryMapper {

    /**
     * Get a cached value.
     *
     * @param entry      cache entry. Must not be <code>null</code>.
     * @param columnName name of the "column" (as if reading from a tabular source). Must not be <code>null</code>.
     * @return mapped value.
     */
    Object getValue(Map.Entry entry, String columnName);
}
