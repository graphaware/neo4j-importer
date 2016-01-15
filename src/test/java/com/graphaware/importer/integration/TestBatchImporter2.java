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
import com.graphaware.importer.importer.Importer;
import com.graphaware.importer.integration.inserter.FriendsImporter;
import com.graphaware.importer.integration.inserter.LocationImporter;
import com.graphaware.importer.integration.inserter.PersonImporter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
}
