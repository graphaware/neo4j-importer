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
