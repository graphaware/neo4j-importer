package com.graphaware.importer.cli;

import com.graphaware.importer.config.ImportConfig;

/**
 * A parser of command line arguments, producing an {@link ImportConfig}.
 *
 * @param <T> type of the produced config.
 */
public interface CommandLineParser<T extends ImportConfig> {

    /**
     * Produce an {@link ImportConfig} from command line arguments.
     *
     * @param args args.
     * @return import context. <code>null</code> if the config could not be produced for whatever reason.
     */
    T parseArgs(String[] args);
}
