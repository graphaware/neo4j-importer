package com.graphaware.importer.integration.inserter;

import com.graphaware.importer.cache.Cache;
import com.graphaware.importer.cache.InjectCache;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.DynamicData;
import com.graphaware.importer.data.access.DataReader;
import com.graphaware.importer.importer.BaseImporter;
import org.neo4j.graphdb.DynamicRelationshipType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FriendsImporter extends BaseImporter<Map<String, Object>> {

    @InjectCache(name = "people")
    private Cache<Long, Long> personCache;

    @Override
    public Data inputData() {
        return DynamicData.withName("friends");
    }

    @Override
    public Map<String, Object> produceObject(DataReader record) {
        Map<String, Object> result = new HashMap<>();

        result.put("id1", record.readLong("id1"));
        result.put("id2", record.readLong("id2"));

        return result;
    }

    @Override
    public void processObject(Map<String, Object> object) {
        context.inserter().createRelationship(personCache.get((long) object.get("id1")), personCache.get((long) object.get("id2")), DynamicRelationshipType.withName("FRIEND_OF"), Collections.<String, Object>emptyMap());
    }
}
