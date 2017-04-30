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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * A {@link FileLocator} that also checks whether the files are actually present. Intended for files (like input files)
 * that must be present for the import, i.e. cannot be created by the importer.
 */
public class InputFileLocator extends FileLocator {

    private static final Logger LOG = LoggerFactory.getLogger(InputFileLocator.class);

    public InputFileLocator(String inputDir, Map<Data, String> fileNames) {
        super(inputDir, fileNames);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Checks for presence of the files and performs additional checks, which subclasses should override.
     */
    @Override
    public void check() {
        LOG.info("Checking files...");

        checkFiles();

        LOG.info("Checking data...");

        checkData();
    }

    /**
     * Check that all files exist.
     *
     * @throws java.lang.IllegalStateException if some files do not exist.
     */
    protected void checkFiles() {
        boolean valid = true;

        for (Data data : allData()) {
            for (String file : locate(data)) {
                if (!new File(file).exists()) {
                    LOG.error("File does not exist: " + file);
                    valid = false;
                }
            }
        }

        if (!valid) {
            LOG.error("There are missing files.");
            throw new IllegalStateException("There are missing files.");
        }
    }

    /**
     * Check contents of the files. No checks performed by default, to be overridden.
     */
    protected void checkData() {
        LOG.info("No data checks.");
    }
}
