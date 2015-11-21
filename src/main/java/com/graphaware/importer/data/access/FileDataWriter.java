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

package com.graphaware.importer.data.access;

import java.io.FileWriter;
import java.io.IOException;

/**
 * A {@link com.graphaware.importer.data.access.DataWriter} that extends a {@link java.io.FileWriter}.
 */
public class FileDataWriter extends FileWriter implements DataWriter {

    private final String fileName;

    /**
     * Constructs a FileWriter object given a file name.
     *
     * @param fileName String The system-dependent filename. Must not be <code>null</code>.
     * @throws IOException if the named file exists but is a directory rather
     *                     than a regular file, does not exist but cannot be
     *                     created, or cannot be opened for any other reason
     */
    public FileDataWriter(String fileName) throws IOException {
        super(fileName);
        this.fileName = fileName;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Translates {@link java.io.IOException} to {@link java.lang.RuntimeException}.
     */
    @Override
    public DataWriter append(String s) {
        try {
            super.append(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "FileDataWriter to " + fileName;
    }
}
