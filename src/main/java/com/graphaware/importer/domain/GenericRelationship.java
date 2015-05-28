package com.graphaware.importer.domain;

import java.util.HashMap;
import java.util.Map;

public abstract class GenericRelationship implements Neo4jRelationship {

    private Long sourceNodeId;
    private Long targetNodeId;
    private final Map<String, Object> properties = new HashMap<>();

    @Override
    public Long sourceKey() {
        return sourceNodeId;
    }

    @Override
    public Long targetKey() {
        return targetNodeId;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setSourceKey(Long sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
    }

    public void setTargetKey(Long targetNodeId) {
        this.targetNodeId = targetNodeId;
    }

    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }
}
