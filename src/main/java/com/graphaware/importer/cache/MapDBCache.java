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

import org.mapdb.DB;
import org.mapdb.Serializer;

import java.util.Map;
import java.util.Set;

/**
 * {@link com.graphaware.importer.cache.Cache} backed by {@link org.mapdb.DB}.
 *
 * @param <K> key type.
 * @param <V> value type.
 */
public class MapDBCache<K, V> implements Cache<K, V> {

    private final Map<K, V> map;

    public MapDBCache(DB db, String name, Serializer<K> keySerializer, Serializer<V> valueSerializer) {
        map = db.hashMap(name).keySerializer(keySerializer).valueSerializer(valueSerializer).create();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public void put(K key, V value) {
        map.put(key, value);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }
}

