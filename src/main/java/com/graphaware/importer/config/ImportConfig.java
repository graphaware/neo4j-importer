package com.graphaware.importer.config;

import com.graphaware.importer.data.access.DataReader;

/**
 * Configuration of an import.
 */
public interface ImportConfig {

    /**
     * @return directory where the database will be stored.
     */
    String getGraphDir();

    /**
     * @return directory where other files produced by the import will be stored.
     */
    String getOutputDir();

    /**
     * @return path to Neo4j properties used during the import.
     */
    String getProps();

    /**
     * Get the data reader that will provide data for this import.
     *
     * @return data reader.
     */
    DataReader createReader();
}
