package com.graphaware.importer.cache;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.util.Assert;

/**
 * {@link com.graphaware.importer.cache.Caches} of type {@link com.graphaware.importer.cache.MapDBCache}.
 */
public class MapDBCaches extends BaseCaches {

    private final DB db;

    public MapDBCaches() {
        db = DBMaker.newMemoryDirectDB().transactionDisable().make();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Cache doCreateCache(String cacheName, Class<?> keyType, Class<?> valueType) {
        return doCreateCache(db, cacheName, keyType, valueType);
    }

    /**
     * Create a cache backed by the given {@link org.mapdb.DB}.
     *
     * @param db        backing the new cache.
     * @param cacheName name of the cache.
     * @param keyType   Java type of the key stored in the cache.
     * @param valueType Java type of the value stored in the cache. Typically a {@link java.lang.Long}.
     * @return cache.
     */
    protected MapDBCache doCreateCache(DB db, String cacheName, Class<?> keyType, Class<?> valueType) {
        return new MapDBCache(db, cacheName, getSerializer(keyType), getSerializer(valueType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void compact() {
        db.compact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        db.close();
    }

    /**
     * Resolve a serializer for a given key or value type.
     *
     * @param type type. Must not be <code>null</code>.
     * @return serializer for the given type.
     */
    protected Serializer<?> getSerializer(Class<?> type) {
        Assert.notNull(type);

        if (String.class.isAssignableFrom(type)) {
            return Serializer.STRING;
        }

        if (Long.class.isAssignableFrom(type)) {
            return Serializer.LONG;
        }

        if (Integer.class.isAssignableFrom(type)) {
            return Serializer.INTEGER;
        }

        return defaultSerializer(type);
    }

    /**
     * Get a serializer for a type, for which a serializer hasn't been resolved by {@link #getSerializer(Class)}.
     *
     * @param type type. Never <code>null</code>.
     * @return serializer for the given type.
     */
    protected Serializer<?> defaultSerializer(Class<?> type) {
        Assert.notNull(type);

        return Serializer.BASIC;
    }
}
