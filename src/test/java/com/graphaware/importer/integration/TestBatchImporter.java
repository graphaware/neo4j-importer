/*
 * Copyright (c) 2013-2016 GraphAware
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

package com.graphaware.importer.integration;

import com.graphaware.importer.FileBatchImporter;
import com.graphaware.importer.config.FileImportConfig;
import com.graphaware.importer.context.CacheAwareImportContext;
import com.graphaware.importer.context.ImportContext;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.DynamicData;
import com.graphaware.importer.data.access.CacheEntryMapper;
import com.graphaware.importer.data.location.DataLocator;
import com.graphaware.importer.data.location.SimpleDataLocator;
import com.graphaware.importer.importer.Importer;
import com.graphaware.importer.integration.inserter.*;

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
                new LocationImporter(),
                new JobsImporter(),
                new LastRoleImporter()
        ));
    }

    @Override
    protected Map<Data, String> input() {
        return DynamicData.oneToOne("people", "jobs", "friends", "locations");
    }

    @Override
    protected ImportContext createContext(FileImportConfig config) {
        return new CacheAwareImportContext(config, createCaches(), createInputDataLocator(config), createOutputDataLocator(config), createCacheInputLocator(), createMapper());
    }

    private DataLocator createCacheInputLocator() {
        return new SimpleDataLocator(Collections.singletonMap(DynamicData.withName("roles"), "roles"));
    }

    private Map<String, CacheEntryMapper> createMapper() {
        return Collections.<String, CacheEntryMapper>singletonMap("roles", new CacheEntryMapper() {
            @Override
            public Object getValue(Map.Entry entry, String columnName) {
                switch (columnName) {
                    case "personId":
                        return entry.getKey();
                    case "position":
                        String[] value = (String[]) entry.getValue();
                        return value[0] + " at " + value[1];
                    default:
                        throw new IllegalStateException("Unknown column " + columnName);
                }
            }
        });
    }
}
