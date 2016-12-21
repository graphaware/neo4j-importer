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

package com.graphaware.importer.cli;

import com.graphaware.importer.config.FileImportConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link com.graphaware.importer.cli.CommandLineParser} for file imports.
 */
public abstract class FileCommandLineParser<T extends FileImportConfig> extends BaseCommandLineParser<T> {

    private static final Logger LOG = LoggerFactory.getLogger(FileCommandLineParser.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected T doProduceConfig(CommandLine line, String graphDir, String outputDir, String props, String cacheFile) throws ParseException {
        String inputDir = getMandatoryValue(line, "i");
        LOG.info("\tInput: " + inputDir);

        return doProduceConfig(line, graphDir, outputDir, props, cacheFile, inputDir);
    }

    /**
     * Produce a config.
     *
     * @param line      the command line.
     * @param graphDir  graph directory, already extracted from the command line.
     * @param outputDir output directory, already extracted from the command line.
     * @param props     path to Neo4j properties, already extracted from the command line.
     * @param inputDir  directory for input files, already extracted from the command line.
     * @return import configuration.
     */
    protected abstract T doProduceConfig(CommandLine line, String graphDir, String outputDir, String props, String cacheFile, String inputDir);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOptions(Options options) {
        options.addOption(new Option("i", "input", true, "use given directory to find input files"));
    }
}
