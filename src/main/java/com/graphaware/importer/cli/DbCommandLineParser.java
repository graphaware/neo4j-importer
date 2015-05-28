package com.graphaware.importer.cli;

import com.graphaware.importer.config.ImportConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link com.graphaware.importer.cli.CommandLineParser} for database imports.
 */
public abstract class DbCommandLineParser extends BaseCommandLineParser {

    private static final Logger LOG = LoggerFactory.getLogger(DbCommandLineParser.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected ImportConfig doProduceConfig(CommandLine line, String graphDir, String outputDir, String props) throws ParseException {
        String host = getMandatoryValue(line, "h");
        String port = getMandatoryValue(line, "t");
        String user = getMandatoryValue(line, "u");
        String password = getMandatoryValue(line, "p");

        LOG.info("\tHost:" + host);
        LOG.info("\tPort:" + port);
        LOG.info("\tUsername:" + user);
        LOG.info("\tPassword: **********");

        return doProduceConfig(line, graphDir, outputDir, props, host, port, user, password);
    }

    /**
     * Produce a config.
     *
     * @param line      the command line.
     * @param graphDir  graph directory, already extracted from the command line.
     * @param outputDir output directory, already extracted from the command line.
     * @param props     path to Neo4j properties, already extracted from the command line.
     * @param host      db host, already extracted from the command line.
     * @param port      db port, already extracted from the command line.
     * @param user      db user, already extracted from the command line.
     * @param password  db password, already extracted from the command line.
     * @return import configuration.
     * @throws ParseException
     */
    protected abstract ImportConfig doProduceConfig(CommandLine line, String graphDir, String outputDir, String props, String host, String port, String user, String password) throws ParseException;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOptions(Options options) {
        super.addOptions(options);

        options.addOption(new Option("h", "host", true, "database host"));
        options.addOption(new Option("t", "port", true, "database port"));
        options.addOption(new Option("u", "username", true, "database username"));
        options.addOption(new Option("p", "password", true, "database encrypted password"));
    }
}
