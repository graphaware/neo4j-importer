package com.graphaware.importer.integration.inserter;

import com.graphaware.importer.cache.Cache;
import com.graphaware.importer.cache.Caches;
import com.graphaware.importer.cache.InjectCache;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.DynamicData;
import com.graphaware.importer.data.access.DataReader;
import com.graphaware.importer.importer.BaseImporter;
import org.neo4j.graphdb.DynamicRelationshipType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.neo4j.graphdb.DynamicLabel.label;

public class PersonImporter extends BaseImporter<Map<String, Object>> {

    @InjectCache(name = "people", creator = true)
    private Cache<Long, Long> personCache;

    @InjectCache(name = "locations")
    private Cache<Long, Long> locationCache;

    @Override
    public Data inputData() {
        return DynamicData.withName("people");
    }

    @Override
    public Map<String, Object> produceObject(DataReader record) {
        Map<String, Object> result = new HashMap<>();

        result.put("id", record.readLong("id"));
        result.put("location", record.readLong("location"));
        result.put("name", record.readString("name"));

        return result;
    }

    @Override
    public void processObject(Map<String, Object> object) {
        Map<String, Object> props = new HashMap<>(object);
        props.remove("location");

        personCache.put((Long) object.get("id"), context.inserter().createNode(props, label("Person")));
        context.inserter().createRelationship(personCache.get((long) object.get("id")), locationCache.get((long) object.get("location")), DynamicRelationshipType.withName("LIVES_IN"), Collections.<String, Object>emptyMap());
    }

    @Override
    protected void createCache(Caches caches, String name) {
        if ("people".equals(name)) {
            caches.createCache(name, Long.class, Long.class);
        } else {
            super.createCache(caches, name);
        }
    }
}
