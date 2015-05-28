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
