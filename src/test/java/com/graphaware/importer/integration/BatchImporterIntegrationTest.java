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

package com.graphaware.importer.integration;

import com.graphaware.test.unit.GraphUnit;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * An integration test for (CSV) batch importer.
 */
public class BatchImporterIntegrationTest {

    @Test
    public void testImport() throws IOException, InterruptedException {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();
        String tmpFolder = temporaryFolder.getRoot().getAbsolutePath();

        String cp = new ClassPathResource("people.csv").getFile().getAbsolutePath();
        String path = cp.substring(0, cp.length() - "people.csv".length());

        try {
            TestBatchImporter.main(new String[]{"-g", tmpFolder + "/graph.db", "-i", path, "-o", tmpFolder, "-r", "neo4j.properties"});
        } catch (Throwable t) {
            fail();
        }

        GraphDatabaseService database = new GraphDatabaseFactory().newEmbeddedDatabase(tmpFolder + "/graph.db");

        GraphUnit.assertSameGraph(database, "CREATE " +
                        "(p1:Person {id: 1, name: 'Michal Bachman', role: 'MD at GraphAware'})," +
                        "(p2:Person {id: 2, name: 'Adam George', role: 'Consultant at GraphAware'})," +
                        "(l1:Location {id: 1, name: 'London'})," +
                        "(l2:Location {id: 2, name: 'Watnall'})," +
                        "(l3:Location {id: 3, name: 'Prague'})," +
                        "(c1:Company {name: 'K+N'})," +
                        "(c2:Company {name: 'GraphAware'})," +
                        "(p1)-[:LIVES_IN]->(l1)," +
                        "(p2)-[:LIVES_IN]->(l2)," +
                        "(p1)-[:FRIEND_OF]->(p2)," +
                        "(p1)-[:WORKS_FOR {role: 'Developer'}]->(c1)," +
                        "(p1)-[:WORKS_FOR {role: 'MD'}]->(c2)," +
                        "(p2)-[:WORKS_FOR {role: 'Developer'}]->(c1)," +
                        "(p2)-[:WORKS_FOR {role: 'Consultant'}]->(c2)"
        );

        database.shutdown();
        temporaryFolder.delete();
    }

    @Test
    public void testImport2() throws IOException, InterruptedException {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();
        String tmpFolder = temporaryFolder.getRoot().getAbsolutePath();

        String cp = new ClassPathResource("people.csv").getFile().getAbsolutePath();
        String path = cp.substring(0, cp.length() - "people.csv".length());

        try {
            TestBatchImporter2.main(new String[]{"-g", tmpFolder + "/graph.db", "-i", path, "-o", tmpFolder, "-r", "neo4j.properties"});
        } catch (Throwable t) {
            fail();
        }

        GraphDatabaseService database = new GraphDatabaseFactory().newEmbeddedDatabase(tmpFolder + "/graph.db");

        GraphUnit.assertSameGraph(database, "CREATE " +
                        "(p1:Person {id: 1, name: 'Michal Bachman'})," +
                        "(p2:Person {id: 2, name: 'Adam George'})," +
                        "(l1:Location {id: 1, name: 'London'})," +
                        "(l2:Location {id: 2, name: 'Watnall'})," +
                        "(l3:Location {id: 3, name: 'Prague'})," +
                        "(p1)-[:LIVES_IN]->(l1)," +
                        "(p2)-[:LIVES_IN]->(l2)," +
                        "(p1)-[:FRIEND_OF]->(p2)"
        );

        database.shutdown();
        temporaryFolder.delete();
    }
}
