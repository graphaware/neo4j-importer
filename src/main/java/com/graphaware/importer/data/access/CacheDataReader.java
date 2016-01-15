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

import com.graphaware.importer.cache.Caches;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Map;

/**
 * A {@link com.graphaware.importer.data.access.TabularDataReader} that reads data from a {@link com.graphaware.importer.cache.Cache}.
 */
public class CacheDataReader implements TabularDataReader {

    private final Caches caches;
    private final Map<String, CacheEntryMapper> mappers;
    private Iterator<Map.Entry> entryIterator;
    private Map.Entry entry;
    private CacheEntryMapper mapper;
    private int entryNumber = 0;

    /**
     * Create a new reader.
     *
     * @param caches  to read from. Must not be <code>null</code>.
     * @param mappers of cached data to "columns". Must not be <code>null</code>.
     */
    public CacheDataReader(Caches caches, Map<String, CacheEntryMapper> mappers) {
        Assert.notNull(caches);
        Assert.notNull(mappers);

        this.caches = caches;
        this.mappers = mappers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        //no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void read(String connectionString, String hint) {
        if (entryIterator != null || entry != null) {
            throw new IllegalStateException("Previous reader hasn't been closed");
        }
        entryIterator = caches.getCache(connectionString).entrySet().iterator();
        mapper = mappers.get(connectionString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        entryIterator = null;
        entry = null;
        entryNumber = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long readLong(String columnName) {
        Long value = (Long) mapper.getValue(entry, columnName);

        if (value == null) {
            return null;
        }

        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer readInt(String columnName) {
        Integer value = (Integer) mapper.getValue(entry, columnName);

        if (value == null) {
            return null;
        }

        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long readDate(String columnName) {
        throw new UnsupportedOperationException("Not yet implemented"); //probably never needed
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readObject(String columnName) {
        String value = (String) mapper.getValue(entry, columnName);

        if (value == null) {
            return null;
        }

        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRow() {
        return entryNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean readRecord() {
        if (!entryIterator.hasNext()) {
            return false;
        }

        entry = entryIterator.next();
        entryNumber++;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRawRecord() {
        return entry.toString();
    }
}
