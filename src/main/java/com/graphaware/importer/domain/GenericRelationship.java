/*
 * Copyright (c) 2013-2016 GraphAware
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
