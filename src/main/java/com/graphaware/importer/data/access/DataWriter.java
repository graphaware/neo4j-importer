package com.graphaware.importer.data.access;

/**
 * A data writer.
 */
public interface DataWriter extends DataAccess {

    /**
     * Append a string to the writer.
     *
     * @param s to append. Must not be <code>null</code>.
     * @return this instance.
     */
    DataWriter append(String s);
}
