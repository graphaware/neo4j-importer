package com.graphaware.importer.integration;

import com.graphaware.importer.FileBatchImporter;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.DynamicData;
import com.graphaware.importer.importer.Importer;
import com.graphaware.importer.integration.inserter.FriendsImporter;
import com.graphaware.importer.integration.inserter.LocationImporter;
import com.graphaware.importer.integration.inserter.PersonImporter;

import java.util.*;

public class TestBatchImporter2 extends FileBatchImporter {

    public static void main(String[] args) {
        new TestBatchImporter2().run(args);
    }

    @Override
    protected Set<Importer> createImporters() {
        return new HashSet<>(Arrays.<Importer>asList(
                new LocationImporter(),
                new PersonImporter(),
                new FriendsImporter()
        ));
    }

    @Override
    protected Map<Data, String> input() {
        return new HashMap<Data, String>() {{
            put(DynamicData.withName("people"), "people");
            put(DynamicData.withName("friends"), "friends");
            put(DynamicData.withName("locations"), "locations");
        }};
    }

    @Override
    protected Map<Data, String> output() {
        return Collections.emptyMap();
    }
}
