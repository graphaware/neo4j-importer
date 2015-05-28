package com.graphaware.importer.config;

/**
 * Base class for {@link com.graphaware.importer.config.ImportConfig} implementations for database-based import.
 */
public abstract class DbImportConfig extends BaseImportConfig {

    private final String dbHost;
    private final String dbPort;
    private final String user;
    private final String password;

    protected DbImportConfig(String graphDir, String outputDir, String props, String dbHost, String dbPort, String user, String password) {
        super(graphDir, outputDir, props);
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.user = user;
        this.password = password;
    }

    public String getDbHost() {
        return dbHost;
    }

    public String getDbPort() {
        return dbPort;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

}
