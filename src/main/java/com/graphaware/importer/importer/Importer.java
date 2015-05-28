package com.graphaware.importer.importer;

import com.graphaware.importer.context.ImportContext;

/**
 * A component responsible for importing a single concern. Typically, a "concern" is a single file or SQL query.
 * Implementations must be thread-safe.
 */
public interface Importer {

    /**
     * Importer state.
     */
    public enum State {
        NOT_STARTED,
        RUNNING,
        FINISHED
    }

    /**
     * Get the name of this importer. Useful for logging, thread naming, etc.
     *
     * @return importer name.
     */
    String name();

    /**
     * Prepare this importer for populating the database. Guaranteed to be called before {@link #performImport()}.
     *
     * @param importContext context.
     */
    void prepare(ImportContext importContext);

    /**
     * Perform the actual import, i.e., create nodes and relationships.
     */
    void performImport();

    /**
     * Create indices (and constraints).
     */
    void createIndices();

    /**
     * Get the current state of this importer.
     *
     * @return state.
     */
    State getState();
}
