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
        map = db.createHashMap(name).keySerializer(keySerializer).valueSerializer(valueSerializer).make();
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

