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

package com.graphaware.importer.integration.inserter;

import com.graphaware.importer.cache.Cache;
import com.graphaware.importer.cache.Caches;
import com.graphaware.importer.cache.InjectCache;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.DynamicData;
import com.graphaware.importer.data.access.TabularDataReader;
import com.graphaware.importer.importer.TabularImporter;

import java.util.HashMap;
import java.util.Map;

import static org.neo4j.graphdb.DynamicLabel.label;

public class LocationImporter extends TabularImporter<Map<String, Object>> {

    @InjectCache(name = "locations", creator = true)
    private Cache<Long, Long> locationCache;

    @Override
    public Data inputData() {
        return DynamicData.withName("locations");
    }

    @Override
    public Map<String, Object> produceObject(TabularDataReader record) {
        Map<String, Object> result = new HashMap<>();

        result.put("id", record.readLong("id"));
        result.put("name", record.readObject("name"));

        return result;
    }

    @Override
    public void processObject(Map<String, Object> object) {
        locationCache.put((Long) object.get("id"), context.inserter().createNode(object, label("Location")));
    }

    @Override
    protected void createCache(Caches caches, String name) {
        if ("locations".equals(name)) {
            caches.createCache(name, Long.class, Long.class);
        }
        else {
            super.createCache(caches, name);
        }
    }
}
