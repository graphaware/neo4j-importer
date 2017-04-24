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

import com.graphaware.importer.util.ReflectionUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.Pair;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Neo4jPropertyContainer {

    private static final Map<String, Set<Pair<Field, String>>> CACHE = new ConcurrentHashMap<>();

    public void populateNode(Node node) {
        Map<String, Object> properties = getProperties();

        for (String key : properties.keySet()) {
            Object value = properties.get(key);
            if (value != null) {
                node.setProperty(key, value);
            }
        }
    }

    public Map<String, Object> getProperties() {
        Map<String, Object> result = new HashMap<>();

        if (!CACHE.containsKey(this.getClass().getCanonicalName())) {
            initializeForClass(this.getClass());
        }

        for (Pair<Field, String> fieldAndName : CACHE.get(this.getClass().getCanonicalName())) {
            try {
                Object value = fieldAndName.first().get(this);
                if (value != null) {
                    result.put(fieldAndName.other(), value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    private synchronized void initializeForClass(Class<?> clazz) {
        if (CACHE.containsKey(clazz.getCanonicalName())) {
            return;
        }

        Set<Pair<Field, String>> fieldsAndNames = new HashSet<>();

        for (Field field : ReflectionUtils.getAllFields(clazz)) {
            Neo4jProperty annotation = field.getAnnotation(Neo4jProperty.class);
            if (annotation == null) {
                continue;
            }

            String fieldName = StringUtils.isEmpty(annotation.name()) ? field.getName() : annotation.name();
            fieldsAndNames.add(Pair.of(field, fieldName));
            field.setAccessible(true);
        }

        CACHE.put(clazz.getCanonicalName(), fieldsAndNames);
    }
}
