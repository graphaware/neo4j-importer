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
    protected T doProduceConfig(CommandLine line, String graphDir, String outputDir, String props) throws ParseException {
        String inputDir = getMandatoryValue(line, "i");
        LOG.info("\tInput: " + inputDir);

        return doProduceConfig(line, graphDir, outputDir, props, inputDir);
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
    protected abstract T doProduceConfig(CommandLine line, String graphDir, String outputDir, String props, String inputDir);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOptions(Options options) {
        options.addOption(new Option("i", "input", true, "use given directory to find input files"));
    }
}
