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

package com.graphaware.importer.cache;

import com.graphaware.importer.importer.Importer;
import com.graphaware.importer.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Abstract base-class for {@link com.graphaware.importer.cache.Caches} implementations.
 */
public abstract class BaseCaches implements Caches {

    private static final Logger LOG = LoggerFactory.getLogger(BaseCaches.class);

    private final Map<String, Cache> caches = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void createCache(String cacheName, Class<?> keyType, Class<?> valueType) {
        Assert.hasLength(cacheName);
        Assert.notNull(keyType);
        Assert.notNull(valueType);

        if (caches.containsKey(cacheName)) {
            throw new IllegalStateException("Cache " + cacheName + " has already been created");
        }

        caches.put(cacheName, doCreateCache(cacheName, keyType, valueType));
    }

    /**
     * Create a cache.
     *
     * @param cacheName name of the cache. Never <code>null</code> or empty.
     * @param keyType   Java type of the key stored in the cache. Never <code>null</code>.
     * @param valueType Java type of the value stored in the cache. Typically a {@link java.lang.Long}. Never <code>null</code>.
     */
    public abstract Cache doCreateCache(String cacheName, Class<?> keyType, Class<?> valueType);

    /**
     * {@inheritDoc}
     */
    @Override
    public Cache getCache(String cacheName) {
        Assert.hasLength(cacheName);

        if (!caches.containsKey(cacheName)) {
            throw new IllegalStateException("Cache " + cacheName + " has not been created");
        }

        return caches.get(cacheName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inject(Importer importer) {
        Assert.notNull(importer);

        for (Field field : ReflectionUtils.getAllFields(importer.getClass())) {
            if (field.getAnnotation(InjectCache.class) != null) {
                field.setAccessible(true);
                try {
                    field.set(importer, getCache(field.getAnnotation(InjectCache.class).name()));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup(Collection<Importer> unfinished) {
        Assert.notNull(unfinished);

        if (unfinished.isEmpty()) {
            LOG.info("No need to clear caches, the import is finished.");
            return;
        }

        Set<String> neededCaches = new HashSet<>();
        for (Importer importer : unfinished) {
            neededCaches.addAll(neededCaches(importer));
        }

        boolean cacheCleared = false;

        for (String candidate : caches.keySet()) {
            if (!neededCaches.contains(candidate) && !getCache(candidate).isEmpty()) {
                LOG.info("Clearing cache: " + candidate);
                getCache(candidate).clear();
                cacheCleared = true;
            }
        }

        if (cacheCleared) {
            compact();
        }
    }

    /**
     * Compact caches after some of them have been cleared, if needed. No-op by default.
     */
    protected void compact() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> neededCaches(Importer importer) {
        Assert.notNull(importer);

        Set<String> result = new HashSet<>();

        for (Field field : ReflectionUtils.getAllFields(importer.getClass())) {
            if (field.getAnnotation(InjectCache.class) != null) {
                result.add(field.getAnnotation(InjectCache.class).name());
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> createdCaches(Importer importer) {
        Assert.notNull(importer);

        Set<String> result = new HashSet<>();

        for (Field field : ReflectionUtils.getAllFields(importer.getClass())) {
            if (field.getAnnotation(InjectCache.class) != null && field.getAnnotation(InjectCache.class).creator()) {
                result.add(field.getAnnotation(InjectCache.class).name());
            }
        }

        return result;
    }
}
