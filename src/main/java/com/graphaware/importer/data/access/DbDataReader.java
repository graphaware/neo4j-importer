package com.graphaware.importer.data.access;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;

/**
 * {@link com.graphaware.importer.data.access.DataReader} for databases.
 */
public abstract class DbDataReader extends BaseDataReader {

    private final String dbHost;
    private final String dbPort;
    private final String user;
    private final String password;
    protected JdbcTemplate jdbcTemplate;

    /**
     * Construct a new reader.
     *
     * @param dbHost   db host. Must not be <code>null</code> or empty.
     * @param dbPort   db port. Must not be <code>null</code> or empty.
     * @param user     db user. Must not be <code>null</code>.
     * @param password db password. Must not be <code>null</code>.
     */
    public DbDataReader(String dbHost, String dbPort, String user, String password) {
        Assert.hasLength(dbHost);
        Assert.hasLength(dbPort);
        Assert.notNull(user);
        Assert.notNull(password);

        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.user = user;
        this.password = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        super.initialize();
        this.jdbcTemplate = createJdbcTemplate();
    }

    /**
     * Create a {@link org.springframework.jdbc.core.JdbcTemplate} used for talking to the database.
     *
     * @return jdbc template.
     */
    protected JdbcTemplate createJdbcTemplate() {
        DataSource dataSource = createDataSource();

        JdbcTemplate result = new JdbcTemplate(dataSource);

        additionalConfig(result);

        return result;
    }

    /**
     * Create a {@link javax.sql.DataSource} used for talking to the database.
     *
     * @return data source.
     */
    protected DataSource createDataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setUrl(getUrl(dbHost, dbPort));
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setDefaultReadOnly(true);
        dataSource.setDefaultAutoCommit(false);

        dataSource.setDriverClassName(getDriverClassName());

        additionalConfig(dataSource);

        return dataSource;
    }

    /**
     * Perform additional configuration on the JDBC template. No-op by default, to be overridden.
     *
     * @param template to perform additional config on.
     */
    protected void additionalConfig(JdbcTemplate template) {
    }

    /**
     * Perform additional configuration on the data source. No-op by default, to be overridden.
     *
     * @param dataSource to perform additional config on.
     */
    protected void additionalConfig(DataSource dataSource) {
    }

    /**
     * Get the class name of the JDBC driver. Must be present on the classpath.
     *
     * @return driver name.
     */
    protected abstract String getDriverClassName();

    /**
     * Create the JDBC connection string (url).
     *
     * @param host host to connect to.
     * @param port port to connect on.
     * @return JDBC connection string.
     */
    protected abstract String getUrl(String host, String port);
}
