package com.graphaware.importer.domain;

public class UndirectedRelationship extends GenericRelationship {

    @Override
    public boolean directed() {
        return false;
    }
}
