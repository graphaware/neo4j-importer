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

package com.graphaware.importer.data.location;

import com.graphaware.importer.data.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A {@link DataLocator} that translates data locations to absolute paths to leaf files within directories.
 */
public class DirCsvLocator extends SimpleDataLocator {

    /**
     * Construct a new locator.
     *
     * @param dirNames map of data to logical file names (i.e. without suffix).
     */
    public DirCsvLocator(Map<Data, String> dirNames) {
        super(dirNames);
    }

    /**
     * {@inheritDoc}
     *
     * @return absolute path to a file.
     */
    @Override
    public String[] locate(Data data) {
        String[] locations = super.locate(data);

        List<String> result = new LinkedList<>();

        for (String location : locations) {
            result.addAll(FileUtils.listFiles(new File(location), new String[]{suffix()}, true).stream().map(File::getAbsolutePath).collect(Collectors.toList()));
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     * Get the suffix appended to logical file names.
     *
     * @return suffix. ".csv" by default.
     */
    protected String suffix() {
        return "csv";
    }
}
