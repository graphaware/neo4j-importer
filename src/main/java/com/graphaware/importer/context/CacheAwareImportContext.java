/*
 * Copyright (c) 2014 GraphAware
 *
 * This file is part of GraphAware.
 *
 * GraphAware is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of
 * the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.graphaware.importer.context;

import com.graphaware.importer.cache.Caches;
import com.graphaware.importer.config.ImportConfig;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.access.CacheDataReader;
import com.graphaware.importer.data.access.CacheEntryMapper;
import com.graphaware.importer.data.access.DataReader;
import com.graphaware.importer.data.location.DataLocator;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * {@link com.graphaware.importer.context.ImportContext} useful for imports where {@link Caches} themselves can serve
 * as data input. In such cases, a {@link com.graphaware.importer.data.location.DataLocator} for the cached data needs
 * to be provided to the constructor, along with a {@link java.util.Map} of {@link com.graphaware.importer.data.access.CacheEntryMapper}s.
 */
public class CacheAwareImportContext extends SimpleImportContext {

    private final DataLocator cacheInputLocator;
    private final Map<String, CacheEntryMapper> cacheEntryMappers;

    /**
     * Create a new context.
     *
     * @param config            import config.
     * @param caches            caches to be used throughout the import.
     * @param inputLocator      component capable of locating input data.
     * @param outputLocator     component capable of locating (creating) output data.
     * @param cacheInputLocator component capable of locating cached input data. Must not be <code>null</code>.
     * @param cacheEntryMappers  mapper for cached data as if they were columns. Must not be <code>null</code>.
     */
    public CacheAwareImportContext(ImportConfig config, Caches caches, DataLocator inputLocator, DataLocator outputLocator, DataLocator cacheInputLocator, Map<String, CacheEntryMapper> cacheEntryMappers) {
        super(config, caches, inputLocator, outputLocator);
        Assert.notNull(cacheInputLocator);
        Assert.notNull(cacheEntryMappers);
        this.cacheInputLocator = cacheInputLocator;
        this.cacheEntryMappers = cacheEntryMappers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataReader doCreateReader(Data data) {
        if (cacheInputLocator.canLocate(data)) {
            return new CacheDataReader(caches(), cacheEntryMappers);
        }

        return super.doCreateReader(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String locate(Data data) {
        if (cacheInputLocator.canLocate(data)) {
            return cacheInputLocator.locate(data);
        }

        return super.locate(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCheck() {
        super.doCheck();

        cacheInputLocator.check();
    }
}
