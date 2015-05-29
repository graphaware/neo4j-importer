/*
 * Copyright (c) 2015 GraphAware
 *
 * This file is part of GraphAware Framework.
 *
 * GraphAware Framework is free software: you can redistribute it and/or modify it under the terms
 *  of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of the
 * GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.graphaware.importer.data.location;

import com.graphaware.importer.data.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * A {@link DataLocator} that translates data locations to absolute paths to files.
 */
public class FileLocator extends SimpleDataLocator {

    private final String dir;

    /**
     * Construct a new locator.
     *
     * @param dir       directory in which to locate files.
     * @param fileNames map of data to logical file names (i.e. without suffix).
     */
    public FileLocator(String dir, Map<Data, String> fileNames) {
        super(fileNames);
        this.dir = dir;
    }

    /**
     * {@inheritDoc}
     *
     * @return absolute path to a file.
     */
    @Override
    public String locate(Data data) {
        String location = super.locate(data);

        try {
            FileUtils.forceMkdir(new File(dir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new File(dir + File.separator + location + suffix()).getAbsolutePath();
    }

    /**
     * Get the suffix appended to logical file names.
     *
     * @return suffix. ".csv" by default.
     */
    protected String suffix() {
        return ".csv";
    }
}
