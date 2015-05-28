package com.graphaware.importer.data.access;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * {@link DataReader} for CSV files.
 */
public class CsvDataReader extends BaseDataReader {

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
    protected String doReadString(String columnName) {
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
