/*
 * Copyright (c) 2013-2015 GraphAware
 *
 * This file is part of the GraphAware Framework.
 *
 * GraphAware Framework is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of the
 * GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
