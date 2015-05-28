package com.graphaware.importer.data.location;

import com.graphaware.importer.data.Data;

import java.util.Set;

/**
 * A locator of {@link com.graphaware.importer.data.Data}, i.e. files, queries, etc.
 */
public interface DataLocator {

    /**
     * Check that data is available and correct. This method is intended to catch all data inconsistencies early in
     * the import process. Check data on best effort basis.
     *
     * @throws java.lang.IllegalStateException in case there is something wrong with the data this locator is supposed to locate.
     */
    void check();

    /**
     * Get all data this locator is able to locate.
     *
     * @return set of locatable data.
     */
    Set<Data> allData();

    /**
     * Check if this locator can locate given data.
     *
     * @param data to check.
     * @return true iff this locator can locate the data.
     */
    boolean canLocate(Data data);

    /**
     * Locate the given data. Depending on the implementation, the location could be a file name, an SQL query, etc.
     *
     * @param data to locate.
     * @return data location.
     * @throws java.lang.IllegalStateException iff {@link #canLocate(com.graphaware.importer.data.Data)} returns <code>false</code>.
     */
    String locate(Data data);
}
