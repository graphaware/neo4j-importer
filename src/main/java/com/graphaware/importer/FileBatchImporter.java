package com.graphaware.importer;

import com.graphaware.importer.cli.CommandLineParser;
import com.graphaware.importer.cli.CsvCommandLineParser;
import com.graphaware.importer.config.FileImportConfig;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.location.DataLocator;
import com.graphaware.importer.data.location.InputFileLocator;

import java.util.Map;

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
     * @return mapping.
     */
    protected abstract Map<Data, String> input();
}
