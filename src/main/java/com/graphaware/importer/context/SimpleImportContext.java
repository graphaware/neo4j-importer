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

package com.graphaware.importer.context;

import com.graphaware.importer.cache.Caches;
import com.graphaware.importer.config.ImportConfig;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.access.DataReader;
import com.graphaware.importer.data.location.DataLocator;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Simplest usable {@link com.graphaware.importer.context.ImportContext} that is aware of caches and I/O.
 */
public class SimpleImportContext extends BaseImportContext {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleImportContext.class);

    private final Caches caches;
    private final DataLocator inputLocator;
    private final DataLocator outputLocator;

    /**
     * Create a new context.
     *
     * @param config        import config. Must not be <code>null</code>.
     * @param caches        caches to be used throughout the import. Must not be <code>null</code>.
     * @param inputLocator  component capable of locating input data. Must not be <code>null</code>.
     * @param outputLocator component capable of locating (creating) output data. Must not be <code>null</code>.
     */
    public SimpleImportContext(ImportConfig config, Caches caches, DataLocator inputLocator, DataLocator outputLocator) {
        super(config);
        this.caches = caches;
        this.inputLocator = inputLocator;
        this.outputLocator = outputLocator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Caches caches() {
        return caches;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final DataReader[] createReaders(Data data) {
        Assert.notNull(data);

        List<DataReader> result = new LinkedList<>();
        String[] locations = locate(data);
        for (String location : locations) {
            LOG.info("Reading " + location);
            DataReader dataReader = doCreateReader(data);
            dataReader.initialize();
            dataReader.read(location, data.name());
            result.add(dataReader);
        }

        return result.toArray(new DataReader[result.size()]);
    }

    /**
     * Create a brand new data reader.
     *
     * @param data for which to create a reader. Never <code>null</code>.
     * @return data reader.
     */
    protected DataReader doCreateReader(Data data) {
        return config.createReader();
    }

    /**
     * Locate data.
     *
     * @param data to locate.
     * @return logical data location.
     */
    protected String[] locate(Data data) {
        return inputLocator.locate(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void preBootstrap() {
        super.preBootstrap();

        deleteGraphDirectory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCheck() {
        super.doCheck();

        inputLocator.check();
        outputLocator.check();
    }

    /**
     * Delete the directory (if exists) where the resulting graph will be stored after the import.
     */
    protected void deleteGraphDirectory() {
        try {
            LOG.info("Deleting " + config.getGraphDir() + "...");
            FileUtils.deleteDirectory(new File(config.getGraphDir()));
        } catch (IOException e) {
            LOG.warn("Could not delete graph directory", e);
            throw new RuntimeException(e);
        }
    }

    protected DataLocator getInputLocator() {
        return inputLocator;
    }

    protected DataLocator getOutputLocator() {
        return outputLocator;
    }
}
