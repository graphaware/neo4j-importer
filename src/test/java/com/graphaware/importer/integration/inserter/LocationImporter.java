package com.graphaware.importer.integration.inserter;

import com.graphaware.importer.cache.Cache;
import com.graphaware.importer.cache.Caches;
import com.graphaware.importer.cache.InjectCache;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.DynamicData;
import com.graphaware.importer.data.access.DataReader;
import com.graphaware.importer.importer.BaseImporter;

import java.util.HashMap;
import java.util.Map;

import static org.neo4j.graphdb.DynamicLabel.label;

public class LocationImporter extends BaseImporter<Map<String, Object>> {

    @InjectCache(name = "locations", creator = true)
    private Cache<Long, Long> locationCache;

    @Override
    public Data inputData() {
        return DynamicData.withName("locations");
    }

    @Override
    public Map<String, Object> produceObject(DataReader record) {
        Map<String, Object> result = new HashMap<>();

        result.put("id", record.readLong("id"));
        result.put("name", record.readString("name"));

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
