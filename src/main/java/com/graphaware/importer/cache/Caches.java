package com.graphaware.importer.cache;

import com.graphaware.importer.importer.Importer;

import java.util.Collection;
import java.util.Set;

/**
 * {@link com.graphaware.importer.cache.Cache}s used by all {@link com.graphaware.importer.importer.Importer}s.
 */
public interface Caches {

    /**
     * Create a cache. Cache has to be created before it can be used and cache names must be unique.
     *
     * @param cacheName name of the cache. Must not be <code>null</code> or empty and must be unique.
     * @param keyType   Java type of the key stored in the cache. Must not be <code>null</code>.
     * @param valueType Java type of the value stored in the cache. Typically a {@link java.lang.Long}. Must not be <code>null</code>.
     * @throws java.lang.IllegalStateException in case a cache with the given name already exists.
     */
    void createCache(String cacheName, Class<?> keyType, Class<?> valueType);

    /**
     * Get cache with the given name.
     *
     * @param cacheName name of the cache to locate. Must not be <code>null</code> or empty.
     * @return cache, never <code>null</code>.
     * @throws java.lang.IllegalStateException in case no such cache exists.
     */
    Cache getCache(String cacheName);

    /**
     * Get names of caches needed by the given inserter.
     * Found from {@link com.graphaware.importer.cache.InjectCache} annotations on fields.
     *
     * @param importer for which to find needed caches. Must not be <code>null</code>.
     * @return names of caches needed by the inserter.
     */
    Set<String> neededCaches(Importer importer);

    /**
     * Get names of caches created by the given inserter.
     * Found from {@link com.graphaware.importer.cache.InjectCache} annotations on fields,
     * where {@link InjectCache#creator()} equals <code>true</code>.
     *
     * @param importer for which to find created caches. Must not be <code>null</code>.
     * @return names of created by the inserter.
     */
    Set<String> createdCaches(Importer importer);

    /**
     * Inject the right caches to the given inserter. Will populate fields annotated with
     * {@link com.graphaware.importer.cache.InjectCache}.
     *
     * @param importer to inject caches to. Must not be <code>null</code>.
     */
    void inject(Importer importer);

    /**
     * Cleanup caches that will no longer be used.
     *
     * @param unfinished inserters that are not yet finished. Must not be <code>null</code>.
     */
    void cleanup(Collection<Importer> unfinished);

    /**
     * Destroy all caches.
     */
    void destroy();
}
