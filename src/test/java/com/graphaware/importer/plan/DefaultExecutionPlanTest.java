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

package com.graphaware.importer.plan;

import com.graphaware.importer.cache.Cache;
import com.graphaware.importer.cache.Caches;
import com.graphaware.importer.cache.InjectCache;
import com.graphaware.importer.cache.MapDBCaches;
import com.graphaware.importer.context.ImportContext;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.DynamicData;
import com.graphaware.importer.data.access.TabularDataReader;
import com.graphaware.importer.importer.Importer;
import com.graphaware.importer.importer.TabularImporter;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link com.graphaware.importer.plan.DefaultExecutionPlan}.
 */
public class DefaultExecutionPlanTest {

    private ImportContext context;

    @Before
    public void setUp() {
        Caches caches = new MapDBCaches();
        context = mock(ImportContext.class);
        when(context.caches()).thenReturn(caches);
    }

    private Set<Importer> importers(Importer... importers) {
        return new HashSet<>(Arrays.asList(importers));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToOrderWhenCreatorIsMissing() {
        Set<Importer> importers = importers(new TestImporter());
        ExecutionPlan plan = new DefaultExecutionPlan(importers, context);
        plan.getOrderedImporters();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToOrderWhenCreatorIsMissing2() {
        Set<Importer> importers = importers(new TestImporter(), new TestImporter2());
        ExecutionPlan plan = new DefaultExecutionPlan(importers, context);
        plan.getOrderedImporters();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToOrderWhenThereIsACycle() {
        Set<Importer> importers = importers(new TestImporter(), new TestImporter2(), new TestImporter3());
        ExecutionPlan plan = new DefaultExecutionPlan(importers, context);
        plan.getOrderedImporters();
    }

    @Test
    public void shouldOrderImporters() {
        Set<Importer> importers = importers(new TestImporter(), new TestImporter2(), new TestImporter4());
        ExecutionPlan plan = new DefaultExecutionPlan(importers, context);
        List<Importer> result = plan.getOrderedImporters();

        assertEquals("TEST4", result.get(0).name());
        assertEquals("TEST2", result.get(1).name());
        assertEquals("TEST1", result.get(2).name());
    }

    @Test
    public void shouldCorrectlyJudgeWhenImporterCanRunAndWhenTheyAreFinished() {
        TestImporter i1 = new TestImporter();
        TestImporter2 i2 = new TestImporter2();
        TestImporter4 i4 = new TestImporter4();

        Set<Importer> importers = importers(i1, i2, i4);
        ExecutionPlan plan = new DefaultExecutionPlan(importers, context);

        assertFalse(plan.allFinished());
        assertTrue(plan.canRun(i4));
        assertFalse(plan.canRun(i1));
        assertFalse(plan.canRun(i2));

        try {
            i4.prepare(context);
        } catch (Exception e) {
            //ok
        }

        try {
            i4.performImport();
        } catch (Exception e) {
            //ok
        }

        try {
            plan.canRun(i4);
            fail();
        } catch (IllegalStateException e) {
            //ok
        }

        assertFalse(plan.allFinished());
        assertFalse(plan.canRun(i1));
        assertTrue(plan.canRun(i2));

        try {
            i2.prepare(context);
        } catch (Exception e) {
            //ok
        }

        try {
            i2.performImport();
        } catch (Exception e) {
            //ok
        }

        assertFalse(plan.allFinished());

        try {
            i1.prepare(context);
        } catch (Exception e) {
            //ok
        }

        try {
            i1.performImport();
        } catch (Exception e) {
            //ok
        }

        assertTrue(plan.allFinished());
    }

    private class TestImporter extends TabularImporter<Object> {

        @InjectCache(name = "C1", creator = true)
        private Cache<Long, Long> c1;

        @InjectCache(name = "C2")
        private Cache<String, Long> c2;

        @Override
        public Data inputData() {
            return DynamicData.withName("TEST1");
        }

        @Override
        public Object produceObject(TabularDataReader record) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void processObject(Object object) {
            throw new UnsupportedOperationException();
        }
    }

    private class TestImporter2 extends TabularImporter<Object> {

        @InjectCache(name = "C2", creator = true)
        private Cache<String, Long> c2;

        @InjectCache(name = "C3")
        private Cache<Long, Long> c3;

        @Override
        public Data inputData() {
            return DynamicData.withName("TEST2");
        }

        @Override
        public Object produceObject(TabularDataReader record) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void processObject(Object object) {
            throw new UnsupportedOperationException();
        }
    }

    private class TestImporter3 extends TabularImporter<Object> {

        @InjectCache(name = "C2")
        private Cache<String, Long> c2;

        @InjectCache(name = "C3", creator = true)
        private Cache<Long, Long> c3;

        @Override
        public Data inputData() {
            return DynamicData.withName("TEST3");
        }

        @Override
        public Object produceObject(TabularDataReader record) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void processObject(Object object) {
            throw new UnsupportedOperationException();
        }
    }

    private class TestImporter4 extends TabularImporter<Object> {

        @InjectCache(name = "C3", creator = true)
        private Cache<Long, Long> c3;

        @Override
        public Data inputData() {
            return DynamicData.withName("TEST4");
        }

        @Override
        public Object produceObject(TabularDataReader record) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void processObject(Object object) {
            throw new UnsupportedOperationException();
        }
    }
}
