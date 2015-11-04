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
import com.graphaware.importer.cache.InjectCache;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.DynamicData;
import com.graphaware.importer.data.access.TabularDataReader;
import com.graphaware.importer.importer.TabularImporter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.neo4j.graphdb.DynamicRelationshipType.withName;

public class FriendsImporter extends TabularImporter<Map<String, Object>> {

    @InjectCache(name = "people")
    private Cache<Long, Long> personCache;

    @Override
    public Data inputData() {
        return DynamicData.withName("friends");
    }

    @Override
    public Map<String, Object> produceObject(TabularDataReader record) {
        Map<String, Object> result = new HashMap<>();

        result.put("id1", record.readLong("id1"));
        result.put("id2", record.readLong("id2"));
        result.put("since", record.readDate("since"));

        return result;
    }

    @Override
    public void processObject(Map<String, Object> object) {
        context.inserter().createRelationship(
                personCache.get((long) object.get("id1")),
                personCache.get((long) object.get("id2")),
                withName("FRIEND_OF"),
                Collections.singletonMap("since", object.get("since")));
    }
}
