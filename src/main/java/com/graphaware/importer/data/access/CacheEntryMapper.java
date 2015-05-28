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
