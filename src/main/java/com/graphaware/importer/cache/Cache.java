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

package com.graphaware.importer.cache;

import java.util.Map;
import java.util.Set;

/**
 * A cache, like a {@link java.util.Map} with {@link #get(Object)} and {@link #containsKey(Object)} supporting <code>null</code> keys,
 * returning <code>null</code> and <code>false</code>, respectively.
 * <p/>
 * Caches are needed to map identifiers from custom data sources to Neo4j node IDs assigned to the nodes upon insertion.
 *
 * @param <K> key type.
 * @param <V> value type.
 */
public interface Cache<K, V> {

    int size();

    boolean isEmpty();

    boolean containsKey(K key);

    V get(K key);

    void put(K key, V value);

    void clear();

    Set<Map.Entry<K, V>> entrySet();
}
