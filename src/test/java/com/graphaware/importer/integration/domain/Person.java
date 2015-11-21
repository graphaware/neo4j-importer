/*
 * Copyright (c) 2013-2015 GraphAware
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

package com.graphaware.importer.integration.domain;

import com.graphaware.importer.domain.Neo4jProperty;
import com.graphaware.importer.domain.Neo4jPropertyContainer;

public class Person extends Neo4jPropertyContainer {

    @Neo4jProperty
    private final Long id;
    @Neo4jProperty
    private final String name;
    @Neo4jProperty
    private final Integer age;

    private final Long location;

    public Person(Long id, String name, Integer age, Long location) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public Long getLocation() {
        return location;
    }
}
