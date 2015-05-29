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
import com.graphaware.importer.data.access.DataReader;
import com.graphaware.importer.importer.BaseImporter;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JobsImporter extends BaseImporter<Map<String, Object>> {

    @InjectCache(name = "people")
    private Cache<Long, Long> personCache;

    @InjectCache(name = "companies", creator = true)
    private Cache<String, Long> companyCache;

    @InjectCache(name = "roles", creator = true)
    private Cache<Long, String[]> lastRoleCache;

    @Override
    public Data inputData() {
        return DynamicData.withName("jobs");
    }

    @Override
    public Map<String, Object> produceObject(DataReader record) {
        Map<String, Object> result = new HashMap<>();

        result.put("personId", record.readLong("person_id"));
        result.put("job", record.readString("job"));
        result.put("company", record.readString("company"));

        return result;
    }

    @Override
    public void processObject(Map<String, Object> object) {
        long personId = (long) object.get("personId");
        String company = (String) object.get("company");

        if (!companyCache.containsKey(company)) {
            long nodeId = context.inserter().createNode(Collections.<String, Object>singletonMap("name", company), DynamicLabel.label("Company"));
            companyCache.put(company, nodeId);
        }

        String jobTitle = (String) object.get("job");
        Long personNodeId = personCache.get(personId);
        context.inserter().createRelationship(personNodeId, companyCache.get(company), DynamicRelationshipType.withName("WORKS_FOR"), Collections.<String, Object>singletonMap("role", jobTitle));

        lastRoleCache.put(personNodeId, new String[]{jobTitle, company});
    }

    @Override
    protected void createCache(Caches caches, String name) {
        if ("companies".equals(name)) {
            caches.createCache(name, String.class, Long.class);
            return;
        }
        if ("roles".equals(name)) {
            caches.createCache(name, Long.class, String[].class);
            return;
        }

        super.createCache(caches, name);
    }
}
