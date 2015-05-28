/*
 * Copyright (c) 2014 GraphAware
 *
 * This file is part of GraphAware.
 *
 * GraphAware is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of
 * the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.graphaware.importer.inserter;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.schema.ConstraintCreator;
import org.neo4j.graphdb.schema.IndexCreator;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchRelationship;

import java.util.Map;

/**
 * A {@link BatchInserter} that has all methods synchronized and delegates to a wrapped {@link BatchInserter}.
 */
public class SynchronizedBatchInserter implements BatchInserter {

    private final BatchInserter batchInserter;

    public SynchronizedBatchInserter(BatchInserter batchInserter) {
        this.batchInserter = batchInserter;
    }

    public BatchInserter getBatchInserter() {
        return batchInserter;
    }

    @Override
    public synchronized long createNode(Map<String, Object> properties, Label... labels) {
        return batchInserter.createNode(properties, labels);
    }

    @Override
    public synchronized void createNode(long id, Map<String, Object> properties, Label... labels) {
        batchInserter.createNode(id, properties, labels);
    }

    @Override
    public synchronized boolean nodeExists(long nodeId) {
        return batchInserter.nodeExists(nodeId);
    }

    @Override
    public synchronized void setNodeProperties(long node, Map<String, Object> properties) {
        batchInserter.setNodeProperties(node, properties);
    }

    @Override
    public synchronized boolean nodeHasProperty(long node, String propertyName) {
        return batchInserter.nodeHasProperty(node, propertyName);
    }

    @Override
    public synchronized void setNodeLabels(long node, Label... labels) {
        batchInserter.setNodeLabels(node, labels);
    }

    @Override
    public synchronized Iterable<Label> getNodeLabels(long node) {
        return batchInserter.getNodeLabels(node);
    }

    @Override
    public synchronized boolean nodeHasLabel(long node, Label label) {
        return batchInserter.nodeHasLabel(node, label);
    }

    @Override
    public synchronized boolean relationshipHasProperty(long relationship, String propertyName) {
        return batchInserter.relationshipHasProperty(relationship, propertyName);
    }

    @Override
    public synchronized void setNodeProperty(long node, String propertyName, Object propertyValue) {
        batchInserter.setNodeProperty(node, propertyName, propertyValue);
    }

    @Override
    public synchronized void setRelationshipProperty(long relationship, String propertyName, Object propertyValue) {
        batchInserter.setRelationshipProperty(relationship, propertyName, propertyValue);
    }

    @Override
    public synchronized Map<String, Object> getNodeProperties(long nodeId) {
        return batchInserter.getNodeProperties(nodeId);
    }

    @Override
    public synchronized Iterable<Long> getRelationshipIds(long nodeId) {
        return batchInserter.getRelationshipIds(nodeId);
    }

    @Override
    public synchronized Iterable<BatchRelationship> getRelationships(long nodeId) {
        return batchInserter.getRelationships(nodeId);
    }

    @Override
    public synchronized long createRelationship(long node1, long node2, RelationshipType type, Map<String, Object> properties) {
        return batchInserter.createRelationship(node1, node2, type, properties);
    }

    @Override
    public synchronized BatchRelationship getRelationshipById(long relId) {
        return batchInserter.getRelationshipById(relId);
    }

    @Override
    public synchronized void setRelationshipProperties(long rel, Map<String, Object> properties) {
        batchInserter.setRelationshipProperties(rel, properties);
    }

    @Override
    public synchronized Map<String, Object> getRelationshipProperties(long relId) {
        return batchInserter.getRelationshipProperties(relId);
    }

    @Override
    public synchronized void removeNodeProperty(long node, String property) {
        batchInserter.removeNodeProperty(node, property);
    }

    @Override
    public synchronized void removeRelationshipProperty(long relationship, String property) {
        batchInserter.removeRelationshipProperty(relationship, property);
    }

    @Override
    public synchronized IndexCreator createDeferredSchemaIndex(Label label) {
        return batchInserter.createDeferredSchemaIndex(label);
    }

    @Override
    public synchronized ConstraintCreator createDeferredConstraint(Label label) {
        return batchInserter.createDeferredConstraint(label);
    }

    @Override
    public synchronized void shutdown() {
        batchInserter.shutdown();
    }

    @Override
    public synchronized String getStoreDir() {
        return batchInserter.getStoreDir();
    }
}
