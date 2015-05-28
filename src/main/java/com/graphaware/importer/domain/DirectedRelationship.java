package com.graphaware.importer.domain;

public class DirectedRelationship extends GenericRelationship {

    @Override
    public boolean directed() {
        return true;
    }
}
