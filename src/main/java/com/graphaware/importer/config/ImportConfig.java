/*
 * Copyright (c) 2013-2016 GraphAware
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

    /**
     * Get full path to the file on disk that will be used as cache.
     *
     * @return path to file.
     */
    String getCacheFile();
}
