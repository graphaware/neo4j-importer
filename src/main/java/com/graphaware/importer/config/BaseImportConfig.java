/*
 * Copyright (c) 2015 GraphAware
 *
 * This file is part of GraphAware Framework.
 *
 * GraphAware Framework is free software: you can redistribute it and/or modify it under the terms
 *  of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of the
 * GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.graphaware.importer.config;

import org.springframework.util.Assert;

/**
 * Base-class for {@link com.graphaware.importer.config.ImportConfig} implementations.
 */
public abstract class BaseImportConfig implements ImportConfig {

    private final String graphDir;
    private final String outputDir;
    private final String props;

    /**
     * Construct a new config.
     *
     * @param graphDir  directory where the database will be stored. Must not be <code>null</code> or empty.
     * @param outputDir directory where other files produced by the import will be stored. Must not be <code>null</code> or empty.
     * @param props     path to Neo4j properties used during the import. Must not be <code>null</code> or empty.
     */
    protected BaseImportConfig(String graphDir, String outputDir, String props) {
        Assert.hasLength(graphDir);
        Assert.hasLength(outputDir);
        Assert.hasLength(props);

        this.graphDir = graphDir;
        this.outputDir = outputDir;
        this.props = props;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGraphDir() {
        return graphDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOutputDir() {
        return outputDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProps() {
        return props;
    }
}
