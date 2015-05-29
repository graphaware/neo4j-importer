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

import com.graphaware.importer.data.access.CsvDataReader;
import com.graphaware.importer.data.access.DataReader;

/**
 * {@link com.graphaware.importer.config.ImportConfig} implementation for CSV file-based import.
 */
public class CsvImportConfig extends FileImportConfig {

    private final char delimiter;
    private final char quote;

    /**
     * Construct a new config.
     *
     * @param graphDir  directory where the database will be stored. Must not be <code>null</code> or empty.
     * @param outputDir directory where other files produced by the import will be stored. Must not be <code>null</code> or empty.
     * @param props     path to Neo4j properties used during the import. Must not be <code>null</code> or empty.
     * @param inputDir  directory where input files will be searched. Must not be <code>null</code> or empty.
     * @param delimiter CSV file delimiter.
     * @param quote     CSV file quote character.
     */
    public CsvImportConfig(String graphDir, String outputDir, String props, String inputDir, char delimiter, char quote) {
        super(graphDir, outputDir, props, inputDir);
        this.delimiter = delimiter;
        this.quote = quote;
    }

    /**
     * Construct a new config with comma as delimiter and a double quote as quote.
     *
     * @param graphDir  directory where the database will be stored. Must not be <code>null</code> or empty.
     * @param outputDir directory where other files produced by the import will be stored. Must not be <code>null</code> or empty.
     * @param props     path to Neo4j properties used during the import. Must not be <code>null</code> or empty.
     * @param inputDir  directory where input files will be searched. Must not be <code>null</code> or empty.
     */
    public CsvImportConfig(String graphDir, String outputDir, String props, String inputDir) {
        this(graphDir, outputDir, props, inputDir, ',', '\"');
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataReader createReader() {
        return new CsvDataReader(delimiter, quote);
    }
}
