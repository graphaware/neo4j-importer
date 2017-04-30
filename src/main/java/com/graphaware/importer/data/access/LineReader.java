/*
 * Copyright (c) 2013-2017 GraphAware
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.util.Iterator;

/**
 * {@link DataReader} for file lines.
 */
public class LineReader implements DataReader<String> {

    private LineIterator records;
    private String record;
    private int counter = 0;

    @Override
    public void initialize() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void read(String connectionString, String hint) {
        if (records != null) {
            throw new IllegalStateException("Previous reader hasn't been closed");
        }

        try {
            records = FileUtils.lineIterator(new File(connectionString));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (records != null) {
            LineIterator.closeQuietly(records);
        }
        records = null;
        record = null;
        counter = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRow() {
        return counter;
    }

    @Override
    public String readObject(String columnName) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean readRecord() {
        if (!records.hasNext()) {
            return false;
        }
        record = records.next();
        counter++;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRawRecord() {
        return record;
    }
}
