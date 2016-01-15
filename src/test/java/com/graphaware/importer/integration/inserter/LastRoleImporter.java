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

package com.graphaware.importer.integration.inserter;

import com.graphaware.common.util.Pair;
import com.graphaware.importer.cache.Cache;
import com.graphaware.importer.cache.InjectCache;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.DynamicData;
import com.graphaware.importer.data.access.TabularDataReader;
import com.graphaware.importer.importer.TabularImporter;

public class LastRoleImporter extends TabularImporter<Pair<Long, String>> {

    @InjectCache(name = "roles")
    private Cache<Long, String[]> lastRoleCache;

    @Override
    public Data inputData() {
        return DynamicData.withName("roles");
    }

    @Override
    public Pair<Long, String> produceObject(TabularDataReader record) {
        Long personId = record.readLong("personId");
        String position = record.readObject("position");

        return new Pair<>(personId, position);
    }

    @Override
    public void processObject(Pair<Long, String> object) {
        context.inserter().setNodeProperty(object.first(), "role", object.second());
    }
}
