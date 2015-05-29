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

package com.graphaware.importer;

import com.graphaware.importer.cli.CommandLineParser;
import com.graphaware.importer.cli.CsvCommandLineParser;
import com.graphaware.importer.config.FileImportConfig;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.DynamicData;
import com.graphaware.importer.data.location.DataLocator;
import com.graphaware.importer.data.location.InputFileLocator;
import com.graphaware.importer.importer.Importer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link com.graphaware.importer.BatchImporter} for file-based imports.
 */
public abstract class FileBatchImporter extends BatchImporter<FileImportConfig> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected CommandLineParser<FileImportConfig> commandLineParser() {
        return new CsvCommandLineParser();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataLocator createInputDataLocator(FileImportConfig config) {
        return new InputFileLocator(config.getInputDir(), input());
    }

    /**
     * Get input data to logical file name mapping.
     *
     * @return mapping. One-to-one mapping of all importers' input data names by default.
     */
    protected Map<Data, String> input() {
        Set<String> inputs = new HashSet<>();

        for (Importer importer : importers()) {
            inputs.add(importer.inputData().name());
        }

        return DynamicData.oneToOne(inputs.toArray(new String[inputs.size()]));
    }
}
