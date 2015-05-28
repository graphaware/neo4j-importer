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
