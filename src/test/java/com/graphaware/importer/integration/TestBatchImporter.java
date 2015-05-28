package com.graphaware.importer.integration;

import com.graphaware.importer.FileBatchImporter;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.DynamicData;
import com.graphaware.importer.importer.Importer;
import com.graphaware.importer.integration.inserter.FriendsImporter;
import com.graphaware.importer.integration.inserter.LocationImporter;
import com.graphaware.importer.integration.inserter.PersonImporter;

import java.util.*;

public class TestBatchImporter extends FileBatchImporter {

    public static void main(String[] args) {
        new TestBatchImporter().run(args);
    }

    @Override
    protected Set<Importer> createImporters() {
        return new HashSet<>(Arrays.<Importer>asList(
                new PersonImporter(),
                new FriendsImporter(),
                new LocationImporter()
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
