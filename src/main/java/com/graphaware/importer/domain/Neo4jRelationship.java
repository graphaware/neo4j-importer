package com.graphaware.importer.domain;

import java.util.Map;

/**
 * A relationship for the purposes of batch import.
 */
public interface Neo4jRelationship {

    /**
     * Get an identifier of the source node, typically a cache key.
     *
     * @return key.
     */
    Long sourceKey();

    /**
     * Get an identifier of the source node, typically a cache key.
     *
     * @return key.
     */
    Long targetKey();

    /**
     * Get the props of the relationship.
     *
     * @return props.
     */
    Map<String, Object> getProperties();

    /**
     * @return true iff directed.
     */
    boolean directed();
}
