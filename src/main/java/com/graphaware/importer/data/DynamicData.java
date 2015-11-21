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

package com.graphaware.importer.data;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Named {@link com.graphaware.importer.data.Data} programmatically created on-the-fly.
 */
public class DynamicData implements Data {

    private final String name;

    /**
     * Create new data.
     *
     * @param name name of the data. Must not be <code>null</code> or empty.
     * @return data object.
     */
    public static Data withName(final String name) {
        return new DynamicData(name);
    }

    /**
     * Create a one-to-one {@link com.graphaware.importer.data.Data} to name map. Convenient when data names and logical
     * (file) names of the data is the same.
     *
     * @param names of data.
     * @return map of data-name pairs (where the name of the data in the key and the value are the same).
     */
    public static Map<Data, String> oneToOne(String... names) {
        Map<Data, String> result = new HashMap<>();
        for (String name : names) {
            result.put(withName(name), name);
        }
        return result;
    }

    /**
     * Create new data.
     *
     * @param name name of the data. Must not be <code>null</code> or empty.
     */
    protected DynamicData(String name) {
        Assert.hasLength(name);

        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DynamicData dynamicData = (DynamicData) o;

        if (!name.equals(dynamicData.name)) return false;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }
}
