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
