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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * {@link TabularDataReader} for CSV files.
 */
public class CsvDataReader extends BaseTabularDataReader {

    private Iterator<CSVRecord> records;
    private CSVRecord record;
    private Reader in;

    private final char delimiter;
    private final char quote;

    /**
     * Create a new reader.
     *
     * @param delimiter delimiter.
     * @param quote     quote character.
     */
    public CsvDataReader(char delimiter, char quote) {
        this.delimiter = delimiter;
        this.quote = quote;
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
            in = new FileReader(connectionString);
            records = CSVFormat.DEFAULT
                    .withDelimiter(delimiter)
                    .withQuote(quote)
                    .withHeader()
                    .parse(in).iterator();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        in = null;
        records = null;
        record = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doReadObject(String columnName) {
        String s = record.get(columnName);

        if (s != null) {
            s = s.replaceAll(System.getProperty("line.separator"), "").trim();
        }

        return s;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRow() {
        return Long.valueOf(record.getRecordNumber()).intValue();
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

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRawRecord() {
        return record.toString();
    }
}
