GraphAware Neo4j Importer
======================================

[![Build Status](https://travis-ci.org/graphaware/neo4j-importer.png)](https://travis-ci.org/graphaware/neo4j-importer) | <a href="http://graphaware.com/products/" target="_blank">Products</a> | Latest Release: 2.3.1.35.2

GraphAware Importer is a high-performance importer for importing data from any data source to Neo4j. It is intended
for imports of large amounts of data (millions to billions of nodes and relationships), which needs to be cleansed,
normalised, or transformed during the import.

### Another Importer?

There are a number of ways of getting data into Neo4j.

* If you have small amounts of CSV data, use Neo4j's LOAD CSV
* If you have large amounts of clean CSV data where you can separate nodes and relationships into different files, use Neo4j's Batch Importer
* If you have large amounts of ready-to-be imported (i.e. not too dirty) data in any tabular form and don't want do code, use GraphAware's Noader
* For all other scenarios, especially if you have large volumes of data from any source (CSV, MySQL, Oracle, HBase, you name it!) that need to be cleansed, normalised or transformed in some way, use this importer. **You will need to code** in Java.



Getting the Software
--------------------


Usage
-----

`java -cp ./path/to/importer/importer.jar com.graphaware.importer.MyBatchImporter`

usage:

```
 -g,--graph <arg>        use given directory to output the graph
 -i,--input <arg>        use given directory to find input files
 -o,--output <arg>       use given directory to output auxiliary files, such as statistics
 -r,--properties <arg>   use given file as neo4j properties
```
