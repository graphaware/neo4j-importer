Further Examples
================

This document showcases some further examples of reading data from alternate data sources. To successfully set up and customize additional database import tools, the `DataReader`, `ImportConfig` and `CommandLineParser` classes must be implemented.

# MySQL Data Source

### `MySqlDataReader` Implementation

The following libraries are required for the `DataReader`.

```java
import com.graphaware.importer.data.access.QueueDbDataReader;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
```

The class itself is similar to that of the Oracle example in [README.md](../README.md). `QueueDbDataReader` is still extended although the MySQL JDBC driver is alternatively used. Note that variable `db` is added to represent the name of the database. The variable `prefetchsize` found in the `OracleDataReader` example is defunct in a MySQL environment and is therefore not included.

```java
public class MySqlDataReader extends QueueDbDataReader {

    private final String db;
    private final int fetchSize;

    public MySqlDataReader(String dbHost, String dbPort, String user, String password, String db, int fetchSize) {
        super (dbHost, dbPort, user, password);
        this.db = db;
        this.fetchSize = fetchSize;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getDriverClassName() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    protected String getUrl(String host, String port) {
        return "jdbc:mysql://" + host + ":" + port + "/" + db;
}

    @Override
    protected void additionalConfig(JdbcTemplate template) {
        template.setFetchSize(fetchSize);
    }

    @Override
    protected void additionalConfig(DataSource dataSource) {
        ((BasicDataSource) dataSource).setInitialSize(1000);
    }
}
```

### `MySqlImportConfig` Implementation

The following libraries are required for the `ImportConfig`.

```java
import com.graphaware.importer.config.DbImportConfig;
import com.graphaware.importer.data.access.DataReader;
```

Again, the class dismissed the `prefetchsize` variable found in the `OracleDataReader` example. The `db` database name variable is added in a similar way.

```java
public class MySqlImportConfig extends DbImportConfig {

    private final String db;
    private final int fetchSize;

    public MySqlImportConfig(String graphDir, String outputDir, String props, String dbHost, String dbPort, String user, String password, String db, int fetchSize) {
        super(graphDir, outputDir, props, dbHost, dbPort, user, password);
        this.db = db;
        this.fetchSize = fetchSize;
    }

    @Override
    public DataReader createReader() {
        return new MySqlDataReader(getDbHost(), getDbPort(), getUser(), getPassword(), getDb(), fetchSize);
    }

    public String getDb() {
        return this.db;
    }
}
```

### `MySqlCommandLineParser` Implementation

The following libraries are required for the `CommandLineParser`.

```java
import com.graphaware.importer.cli.DbCommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
```

The `CommandLineParser` is the first point of interaction between user input and the program. It therefore has been slightly modified to idiomatically interpret a MySQL data source.

```java
public class MySqlCommandLineParser extends DbCommandLineParser {

    @Override
    protected MySqlImportConfig doProduceConfig(CommandLine line, String graphDir, String outputDir, String props, String host, String port, String user, String password) throws ParseException {
        String db = String.valueOf(getOptionalValue(line, "db", ""));
        int fetchSize = Integer.valueOf(getOptionalValue(line, "fs", "10000"));

        return new MySqlImportConfig(graphDir, outputDir, props, host, port, user, password, db, fetchSize);
    }

    @Override
    protected void addOptions(Options options) {
        super.addOptions(options);
        options.addOption(new Option("db", "db", true, "database name"));
        options.addOption(new Option("fs", "fetchSize", true, "JDBC driver row fetch size (default 10000)"));
    }
}
```