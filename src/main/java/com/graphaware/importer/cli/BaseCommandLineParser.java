package com.graphaware.importer.cli;

import com.graphaware.importer.config.ImportConfig;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base-class for implementations of {@link com.graphaware.importer.cli.CommandLineParser}.
 *
 * @param <T> type of the produced config.
 */
public abstract class BaseCommandLineParser<T extends ImportConfig> implements CommandLineParser<T> {

    private static final Logger LOG = LoggerFactory.getLogger(BaseCommandLineParser.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public final T parseArgs(String[] args) {
        Options options = produceOptions();

        try {
            return produceConfig(args, options);
        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("importer", options);
            return null;
        }
    }

    private T produceConfig(String[] args, Options options) throws ParseException {
        CommandLine line = new BasicParser().parse(options, args);

        String graphDir = getMandatoryValue(line, "g");
        String outputDir = getMandatoryValue(line, "o");
        String props = getMandatoryValue(line, "r");

        LOG.info("Producing import config:");
        LOG.info("\tGraph: " + graphDir);
        LOG.info("\tOutput: " + outputDir);
        LOG.info("\tProps: " + props);

        return doProduceConfig(line, graphDir, outputDir, props);
    }

    /**
     * Produce a config.
     *
     * @param line      the command line.
     * @param graphDir  graph directory, already extracted from the command line.
     * @param outputDir output directory, already extracted from the command line.
     * @param props     path to Neo4j properties, already extracted from the command line.
     * @return import configuration.
     * @throws ParseException
     */
    protected abstract T doProduceConfig(CommandLine line, String graphDir, String outputDir, String props) throws ParseException;

    /**
     * Produce default/essential options.
     *
     * @return command line options.
     */
    private Options produceOptions() {
        Options options = new Options();
        options.addOption(new Option("g", "graph", true, "use given directory to output the graph"));
        options.addOption(new Option("o", "output", true, "use given directory to output auxiliary files, such as statistics"));
        options.addOption(new Option("r", "properties", true, "use given file as neo4j properties"));

        addOptions(options);

        return options;
    }

    /**
     * Add extra options to the default ones. No-op by default, intended to be overridden.
     *
     * @param options default options.
     */
    protected void addOptions(Options options) {

    }

    /**
     * Convenience method for getting an optional command line value.
     *
     * @param line         command line.
     * @param opt          option.
     * @param defaultValue if the option isn't present.
     * @return value.
     */
    protected final String getOptionalValue(CommandLine line, String opt, String defaultValue) {
        return line.getOptionValue(opt, defaultValue);
    }

    /**
     * Convenience method for getting a mandatory command line value.
     *
     * @param line command line.
     * @param opt  option.
     * @return value.
     * @throws org.apache.commons.cli.ParseException if the option is missing.
     */
    protected final String getMandatoryValue(CommandLine line, String opt) throws ParseException {
        String result = line.getOptionValue(opt);
        if (result == null) {
            throw new ParseException("Missing option: " + opt);
        }
        return result;
    }
}
