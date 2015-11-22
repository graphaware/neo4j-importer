GraphAware Neo4j Importer
======================================

[![Build Status](https://travis-ci.org/graphaware/neo4j-importer.png)](https://travis-ci.org/graphaware/neo4j-importer) | <a href="http://graphaware.com/products/" target="_blank">Products</a> | Latest Release: 2.3.1.36.2

GraphAware Importer is a high-performance importer for importing data from any data source to Neo4j. It is intended
for initial one-off imports of large amounts of data (millions to billions of nodes and relationships), which needs
to be cleansed, normalised, or transformed during the import. Depending on many things (connection speed, database speed,
query complexity, data quality,...), you'll be able to import millions of nodes and relationships in minutes.

### Another Importer?

There are a number of ways of getting data into Neo4j.

* If you have small amounts of CSV data, use [Neo4j's LOAD CSV](http://neo4j.com/docs/stable/query-load-csv.html)
* If you have large amounts of clean CSV data where you can separate nodes and relationships into different files, use [Neo4j's Import Tool](http://neo4j.com/docs/stable/import-tool.html)
* If you have large amounts of ready-to-be imported (i.e. not too dirty) data in any tabular form and don't want do code, use GraphAware's Neo4j DataBridge (coming soon)
* For all other scenarios, especially if you have large volumes of data from any source (CSV, MySQL, Oracle, HBase, you name it!) that need to be cleansed, normalised or transformed in some way, use this importer. **You will need to code** in Java.

### Tutorial

This tutorial will guide you through writing an efficient one-off importer of data into Neo4j in a short amount of time.
**You need to be able to write some Java.** What you will get at the end of the process is a standalone Java application
that you can invoke from the command line. It will import data from a data source of your choice and create a brand new
fully usable Neo4j database on disk. It is using [Neo4j's BatchInserter](http://neo4j.com/docs/stable/batchinsert.html)
under the hood.

This tool **will not** be able to import into an existing database, or a running Neo4j instance (yet).

#### Step 0: Get Data

You need some data of course. For this tutorial, we're going to be importing from 2 CSV files:

people-file.csv:
```
"id","name","location","age"
"1","Michal Bachman","1",30
"2","Adam George","2",29
"","PersonWithNoId","2",99
"4","  ","2",100
```

locations-file.csv:
```
"id","name"
"1","London"
"2","Watnall"
"3","Prague"
```

In practice, these could be tables from (or queries against) a relational database, column families from Cassandra, you name it.

The graph we're looking to get by importing the files above is:
```
CREATE
(m:Person {id:1, name:'Michal Bachman', age:30}),
(a:Person {id:2, name:'Adam George', age:29}),
(l:City {id:1, name:'London'}),
(w:City {id:2, name:'Watnall'}),
(p:City {id:3, name:'Prague'}),
(m)-[:LIVES_IN]->(l),
(a)-[:LIVES_IN]->(w)
```

Note that the last two lines in people-file.csv are bad data, we don't want to import these.

#### Step 1: New Project

Create a brand new Java project and bring this project as its dependency. Assuming you're using Maven, declare the following
dependency in your pom.xml

```
<dependency>
    <groupId>com.graphaware.neo4j</groupId>
    <artifactId>programmatic-importer</artifactId>
    <version>2.3.1.36.2</version>
</dependency>
```

You will also need to make sure that the .jar file produced at the end of the process is a "fat jar", i.e. that it contains
all the needed dependencies. For this to happen, you need something like this in your pom.xml:

```
<build>
    <plugins>
        <plugin>
            <artifactId>maven-assembly-plugin</artifactId>
            <version>2.4.1</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>attached</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <finalName>my-importer</finalName>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
                <appendAssemblyId>false</appendAssemblyId>
            </configuration>
        </plugin>
    </plugins>
</build>
```

#### Step 2: Data Reader

Implement a `DataReader` that is able to read from your data source. Most readers will be `TabularDataReader`s. If you're
importing from a CSV file, you can skip this step and use the provided `CsvDataReader`. If you're importing from a relational database,
you can save some time by extending `DbDataReader` or `QueueDbDataReader` (recommended).

For example, it you're reading from Oracle, your data reader will look something like this:

```java
/**
 * {@link com.graphaware.importer.data.access.DbDataReader} for Oracle.
 */
public class OracleDataReader extends QueueDbDataReader {

    private final String db;
    private final int prefetchSize;
    private final int fetchSize;

    public OracleDataReader(String dbHost, String dbPort, String user, String password, String db, int prefetchSize, int fetchSize) {
        super(dbHost, dbPort, user, password);
        this.db = db;
        this.prefetchSize = prefetchSize;
        this.fetchSize = fetchSize;
    }

    @Override
    protected String getDriverClassName() {
        return "oracle.jdbc.OracleDriver";
    }

    @Override
    protected String getUrl(String host, String port) {
        return "jdbc:oracle:thin:@//" + host + ":" + port + "/" + db;
    }

    @Override
    protected void additionalConfig(JdbcTemplate template) {
        template.setFetchSize(fetchSize);
    }

    @Override
    protected void additionalConfig(DataSource dataSource) {
        ((BasicDataSource) dataSource).addConnectionProperty("defaultRowPrefetch", String.valueOf(prefetchSize));
        ((BasicDataSource) dataSource).setInitialSize(10);
    }
}
```

Note that you will have to add the driver (Oracle JDBC driver in this case) into your Maven dependencies.

If you're writing an importer for a non-relational database, for example HBase, you will need to do a bit more work. An
example HBase data reader would look like this:

```java
import com.graphaware.importer.data.access.DataReader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;

public class HbaseDataReader implements DataReader<Map<String, Collection<String>>> {

    private final Configuration configuration;
    private final String columnFamily;
    private Connection connection;
    private ResultScanner scanner;
    private Iterator<Result> results = null;
    private Result result = null;
    private int row = 0;

    public HbaseDataReader(Configuration configuration, String columnFamily) {
        this.configuration = configuration;
        this.columnFamily = columnFamily;
    }

    @Override
    public void initialize() {

    }

    @Override
    public Map<String, Collection<String>> readObject(String columnFamily) {
        Set<String> cells = new HashSet<>();

        for (byte[] cell : result.getFamilyMap(Bytes.toBytes(columnFamily)).keySet()) {
            cells.add(Bytes.toString(cell));
        }

        String key = Bytes.toString(result.getRow());

        return Collections.<String, Collection<String>>singletonMap(key, cells);
    }

    @Override
    public void read(String connectionString, String hint) {
        if (results != null) {
            throw new IllegalStateException("Previous reader hasn't been closed");
        }

        try {
            connection = ConnectionFactory.createConnection(configuration);
            Table table = connection.getTable(TableName.valueOf(connectionString));
            Scan scan = new Scan();
            scan.addFamily(Bytes.toBytes(columnFamily));
            scanner = table.getScanner(scan);
            results = scanner.iterator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        scanner.close();

        try {
            connection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        results = null;
        result = null;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public boolean readRecord() {
        if (!results.hasNext()) {
            return false;
        }
        result = results.next();
        row++;

        return true;
    }

    @Override
    public String getRawRecord() {
        return result.toString();
    }
```

#### Step 3: Domain

You now need to define some Java classes that represent the things you are going to be importing. The data from the reader
will be translated to these classes. Validation, normalization, and transformation can be applied to these classes, before
they are translated into Neo4j nodes and relationships.

If you don't need to apply much logic to the data, you can choose to go with `Map<String, Object>` instead of concrete objects.
The `String` in the map is some property key and the `Object` is that property's value.

Let's assume location data is clean, so we'll go with the `Map` approach. For importing people, we choose to create a class like this:

```java
public class Person extends Neo4jPropertyContainer {

    @Neo4jProperty
    private final Long id;
    @Neo4jProperty
    private final String name;
    @Neo4jProperty
    private final Integer age;

    private final Long location;

    public Person(Long id, String name, Integer age, Long location) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public Long getLocation() {
        return location;
    }
}
```

In this case, we're expecting each row from the data source to contain four pieces of information (id, name, age, location).
The ones that we want to become a node's properties in Neo4j, we annotate with `@Neo4jProperty`. The `location` property will not
be stored in Neo4j, it will be used to link the person to a location, so it is not annotated. Choose the names of the properties
according to how they will be called in Neo4j - it doesn't matter at this point what they are called in your source database.

#### Step 4: Importers

Now you define the actual import logic. For each domain class from the previous step, there should be one `Importer`.
Importers should extend `BaseImporter`. If using `TabularDataReader`, you can extend `TabularImporter` instead.
For locations and people, we will write the two importers. Don't get scared, we will explain all aspects of
writing such importers step-by-step.

```java
public class LocationImporter extends TabularImporter<Map<String, Object>> {

    @InjectCache(name = "locations", creator = true)
    private Cache<Long, Long> locationCache;

    @Override
    public Data inputData() {
        return DynamicData.withName("locations");
    }

    @Override
    public Map<String, Object> produceObject(TabularDataReader record) {
        Map<String, Object> result = new HashMap<>();

        result.put("id", record.readLong("id"));
        result.put("name", record.readObject("name"));

        return result;
    }

    @Override
    public void processObject(Map<String, Object> object) {
        locationCache.put((Long) object.get("id"), context.inserter().createNode(object, DynamicLabel.label("Location")));
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
```

Let's start with the `LocationImporter` above. We've decided earlier not to create a dedicated "domain" object for locations.
We're importing from tabular data (CSV), therefore we will extend `TabularImporter<Map<String, Object>>`.

There are two important methods that need to be implemented first. `produceObject(..)` will produce a "domain" object from
a tabular record. `processObject(..)` should validate and normalize the object and insert it into Neo4j.

Producing the object should be a trivial mapping exercise, reading values from the (database/csv) record and populating
our object with it. Populating it with dirty data is fine at this point, but `null` can be returned if we don't really
want to produce an object from the record, because it is apparently wrong.

Processing the object means a couple of things. The minimum we should do is create a Location node from the object
by writing: `context.inserter().createNode(object, label("Location")`. This will create a new node with label "Location"
and properties in the `Map` - "id" and "name" in this case. This method call returns the Neo4j node ID of the newly created
node.

Since we will need to link people to locations later on, we should remember what Neo4j node ID was assigned to our the each
location. Remember the "id" property of the location is coming from our relational data. For this reason, we need to have
an (off-heap) `Cache` in place:

```java
@InjectCache(name = "locations", creator = true)
private Cache<Long, Long> locationCache;
```

This tells the importer infrastructure that a cache called "locations" is used by this importer and that the key (own ID)
is a `Long`. The value is usually a `Long`, because it is the Neo4j node ID. Moreover, `creator=true` tells the infrastructure
that this importer creates this cache. That means other importers that need this cache will need to run after this one
has finished. For each cache, there can only ever be a single creator.

When an importer is a cache creator, it needs to actually create the cache by implementing the `createCache(..)` method.
It should check that it is asked to create the right one. If not, it should delegate to super-class, e.g.:

```java
@Override
protected void createCache(Caches caches, String name) {
    if ("locations".equals(name)) {
        caches.createCache(name, Long.class, Long.class);
    }
    else {
        super.createCache(caches, name);
    }
}
```

With the caches explained, we will refine our node creating method to populate the cache with each new location:

```java
@Override
public void processObject(Map<String, Object> object) {
    locationCache.put((Long) object.get("id"), context.inserter().createNode(object, label("Location")));
}
```

Finally, each importer needs to implement the `inputData()` method to indicate, what sort of input data it works with.
This is later used to actually find the data. So "locations" here could represent a CSV file called "locations-file.csv", or
a SQL query "SELECT * FROM locations", etc...

With this in mind, let's have a look at the slightly more complicated implementation of `PersonImporter`:

```java
public class PersonImporter extends TabularImporter<Person> {

    @InjectCache(name = "people", creator = true)
    private Cache<Long, Long> personCache;

    @InjectCache(name = "locations")
    private Cache<Long, Long> locationCache;

    @Override
    public Data inputData() {
        return DynamicData.withName("people");
    }

    @Override
    public Person produceObject(TabularDataReader record) {
        //for demo purposes, let's say we can't construct a person without ID
        if (record.readLong("id") == null) {
            return null;
        }
        return new Person(record.readLong("id"), record.readObject("name"), record.readInt("age"), record.readLong("location"));
    }

    @Override
    public void processObject(Person person) {
        //for demo purposes, let's say people with empty names are invalid.
        if (StringUtils.isEmpty(person.getName())) {
            throw new RuntimeException("Person has empty name");
        }

        personCache.put(person.getId(), context.inserter().createNode(person.getProperties(), label("Person")));
        context.inserter().createRelationship(personCache.get(person.getId()), locationCache.get(person.getLocation()), withName("LIVES_IN"), Collections.<String, Object>emptyMap());
    }

    @Override
    protected void createCache(Caches caches, String name) {
        if ("people".equals(name)) {
            caches.createCache(name, Long.class, Long.class);
        } else {
            super.createCache(caches, name);
        }
    }

    @Override
    public void createIndices() {
        createIndex(label("Person"), "name");
    }
}
```

This importer is producing a person cache and using a location cache to create relationships between people and locations.
It also overrides to `createIndices()` method to create an index on people's names.

#### Step 5: Wiring it all together

Finally, we need to create the actual main importer class that will be called when data is to be imported. In our simple
case, it will look as follows:

```java
public class MyBatchImporter extends FileBatchImporter {

    public static void main(String[] args) {
        new MyBatchImporter().run(args);
    }

    @Override
    protected Set<Importer> createImporters() {
        //list all importers, order does not matter
        return new HashSet<>(Arrays.<Importer>asList(
                new PersonImporter(),
                new LocationImporter()
        ));
    }

    @Override
    protected Map<Data, String> input() {
        //map logical input names to physical ones (file names, queries,...)

        Map<Data, String> map = new HashMap<>();
        map.put(DynamicData.withName("people"), "people-file");
        map.put(DynamicData.withName("locations"), "locations-file");
        return map;
    }
}
```

#### Step 6: Tests

We should now test our importer. This isn't hard. We will be using GraphUnit to do that, so you should have that in your
dependencies:

```
<dependency>
    <groupId>com.graphaware.neo4j</groupId>
    <artifactId>tests</artifactId>
    <version>2.3.1.36</version>
    <scope>test</scope>
</dependency>
```

The test would use the inserter on our csv data and verify the contents of the produced database:

```java
@Test
public void testImport() throws IOException, InterruptedException {
    TemporaryFolder temporaryFolder = new TemporaryFolder();
    temporaryFolder.create();
    String tmpFolder = temporaryFolder.getRoot().getAbsolutePath();

    String cp = new ClassPathResource("people-file.csv").getFile().getAbsolutePath();
    String path = cp.substring(0, cp.length() - "people-file.csv".length());

    try {
        TestBatchImporter.main(new String[]{"-g", tmpFolder + "/graph.db", "-i", path, "-o", tmpFolder, "-r", "neo4j.properties"});
    } catch (Throwable t) {
        fail();
    }

    GraphDatabaseService database = new GraphDatabaseFactory().newEmbeddedDatabase(tmpFolder + "/graph.db");

    GraphUnit.assertSameGraph(database, "CREATE " +
                    "(p1:Person {id: 1, name: 'Michal Bachman', age:30})," +
                    "(p2:Person {id: 2, name: 'Adam George', age:29})," +
                    "(l1:Location {id: 1, name: 'London'})," +
                    "(l2:Location {id: 2, name: 'Watnall'})," +
                    "(l3:Location {id: 3, name: 'Prague'})," +
                    "(p1)-[:LIVES_IN]->(l1)," +
                    "(p2)-[:LIVES_IN]->(l2)"
    );

    database.shutdown();
    temporaryFolder.delete();
}
```

#### Step 7: Use

`java -cp ./path/to/importer/importer.jar com.graphaware.importer.MyBatchImporter`

usage:

```
 -g,--graph <arg>        use given directory to output the graph
 -i,--input <arg>        use given directory to find input files
 -o,--output <arg>       use given directory to output auxiliary files, such as statistics
 -r,--properties <arg>   use given file as neo4j properties
```

#### Step 8: Further Customization

todo: talk about custom Context, custom CommandLineParser,...