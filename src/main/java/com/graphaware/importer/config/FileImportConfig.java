package com.graphaware.importer.config;

/**
 * Base class for {@link com.graphaware.importer.config.ImportConfig} implementations for file-based import.
 */
public abstract class FileImportConfig extends BaseImportConfig {

    private final String inputDir;

    /**
     * Construct a new config.
     *
     * @param graphDir  directory where the database will be stored. Must not be <code>null</code> or empty.
     * @param outputDir directory where other files produced by the import will be stored. Must not be <code>null</code> or empty.
     * @param props     path to Neo4j properties used during the import. Must not be <code>null</code> or empty.
     * @param inputDir  directory where the input files will be read from.
     */
    protected FileImportConfig(String graphDir, String outputDir, String props, String inputDir) {
        super(graphDir, outputDir, props);
        this.inputDir = inputDir;
    }

    public String getInputDir() {
        return inputDir;
    }
}
