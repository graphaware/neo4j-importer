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
