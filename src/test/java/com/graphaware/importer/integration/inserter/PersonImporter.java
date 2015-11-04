/*
 * Copyright (c) 2015 GraphAware
 *
 * This file is part of GraphAware Framework.
 *
 * GraphAware Framework is free software: you can redistribute it and/or modify it under the terms
 *  of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of the
 * GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.graphaware.importer.integration.inserter;

import com.graphaware.importer.cache.Cache;
import com.graphaware.importer.cache.Caches;
import com.graphaware.importer.cache.InjectCache;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.DynamicData;
import com.graphaware.importer.data.access.TabularDataReader;
import com.graphaware.importer.importer.TabularImporter;
import com.graphaware.importer.integration.domain.Person;
import org.springframework.util.StringUtils;

import java.util.Collections;

import static org.neo4j.graphdb.DynamicLabel.label;
import static org.neo4j.graphdb.DynamicRelationshipType.withName;

public class PersonImporter extends TabularImporter<Person> {

    @InjectCache(name = "people", creator = true)
    private Cache<Long, Long> personCache;

    @InjectCache(name = "locations")
    private Cache<Long, Long> locationCache;

    @Override
    public Data inputData() {
        return DynamicData.withName("people");
    }

    @Override
    public Person produceObject(TabularDataReader record) {
        //for testing purposes, let's say we can't construct a person without ID
        if (record.readLong("id") == null) {
            return null;
        }
        return new Person(record.readLong("id"), record.readObject("name"), record.readInt("age"), record.readLong("location"));
    }

    @Override
    public void processObject(Person person) {
        //for testing purposes, let's say people with empty names are invalid.
        if (StringUtils.isEmpty(person.getName())) {
            throw new RuntimeException("Person has empty name");
        }

        personCache.put(person.getId(), context.inserter().createNode(person.getProperties(), label("Person")));
        context.inserter().createRelationship(personCache.get(person.getId()), locationCache.get(person.getLocation()), withName("LIVES_IN"), Collections.<String, Object>emptyMap());
    }

    @Override
    protected void createCache(Caches caches, String name) {
        if ("people".equals(name)) {
            caches.createCache(name, Long.class, Long.class);
        } else {
            super.createCache(caches, name);
        }
    }

    @Override
    public void createIndices() {
        createIndex(label("Person"), "name");
    }
}
