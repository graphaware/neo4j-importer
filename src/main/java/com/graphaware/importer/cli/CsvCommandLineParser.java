package com.graphaware.importer.cli;

import com.graphaware.importer.config.CsvImportConfig;
import com.graphaware.importer.config.FileImportConfig;
import org.apache.commons.cli.CommandLine;

/**
 * Implementation of {@link com.graphaware.importer.cli.CommandLineParser} for CSV imports.
 */
public class CsvCommandLineParser extends FileCommandLineParser<FileImportConfig> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected FileImportConfig doProduceConfig(CommandLine line, String graphDir, String outputDir, String props, String inputDir) {
        return new CsvImportConfig(graphDir, outputDir, props, inputDir);
    }
}
