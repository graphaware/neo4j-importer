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

package com.graphaware.importer.domain;

import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * Unit test for {@link Neo4jPropertyContainer}.
 */
public class Neo4jPropertyContainerTest {

    @Test
    public void classWithNoAnnotationsShouldProduceNoProperties() {
        assertTrue(new NoAnnotations().getProperties().isEmpty());
    }

    @Test
    public void classWithNoFieldsShouldProduceNoProperties() {
        assertTrue(new NoFields().getProperties().isEmpty());
    }

    @Test
    public void classWithAnnotationsShouldProduceProperties() {
        Map<String, Object> result = new WithFields().getProperties();

        assertEquals(2, result.size());
        assertEquals("test", result.get("stringField"));
        assertEquals(2, result.get("testField"));
    }

    @Test
    public void subClassShouldProduceProperties() {
        Map<String, Object> result = new WithInheritedFields().getProperties();

        assertEquals(2, result.size());
        assertEquals("test", result.get("stringField"));
        assertEquals(2, result.get("testField"));
    }

    @Test
    public void classWithAnnotationsShouldProducePropertiesMultiThreaded() {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        final AtomicBoolean failed = new AtomicBoolean(false);

        for (int i = 0; i < 1000; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Map<String, Object> result = new WithFields().getProperties();

                        assertEquals(2, result.size());
                        assertEquals("test", result.get("stringField"));
                        assertEquals(2, result.get("testField"));
                    } catch (Throwable throwable) {
                        failed.set(true);
                    }
                }
            });
        }

        assertFalse(failed.get());
    }

    private class NoAnnotations extends Neo4jPropertyContainer {
        private String field1;
    }

    private class NoFields extends Neo4jPropertyContainer {
    }

    private class WithFields extends Neo4jPropertyContainer {

        @Neo4jProperty
        private String stringField = "test";

        @Neo4jProperty(name = "testField")
        private int intField = 2;

        @Neo4jProperty
        private Long longField = null;
    }

    private class WithInheritedFields extends WithFields {

    }

}
