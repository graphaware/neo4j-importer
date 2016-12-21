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

package com.graphaware.importer.cache;

import com.graphaware.importer.config.ImportConfig;
import com.graphaware.importer.data.Data;
import com.graphaware.importer.data.access.TabularDataReader;
import com.graphaware.importer.importer.Importer;
import com.graphaware.importer.importer.TabularImporter;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link com.graphaware.importer.cache.BaseCaches}.
 */
public class CachesTest {

    private ImportConfig config;

    @Before
    public void setUp() {
        config = mock(ImportConfig.class);

        when(config.getCacheFile()).thenReturn("/tmp/cache");
    }

    @Test
    public void shouldCorrectlyReportNeededCaches() {
        TestImporter inserter = new TestImporter();

        MapDBCaches mapDBCaches = new MapDBCaches(config);

        Set<String> neededCaches = mapDBCaches.neededCaches(inserter);

        assertEquals(2, neededCaches.size());
        assertTrue(neededCaches.contains("C1"));
        assertTrue(neededCaches.contains("C2"));

        mapDBCaches.destroy();
    }

    @Test
    public void shouldInjectCaches() {
        Caches caches = new MapDBCaches(config) {{
            createCache("C1", Long.class, Long.class);
            createCache("C2", String.class, Long.class);
        }};

        Cache c1 = caches.getCache("C1");
        Cache c2 = caches.getCache("C2");

        TestImporter inserter = new TestImporter();

        caches.inject(inserter);

        assertEquals(c1, inserter.candidates);
        assertEquals(c2, inserter.companies);

        caches.destroy();
    }

    @Test
    public void shouldClearUnusedCaches() {
        Caches caches = new MapDBCaches(config) {{
            createCache("C1", Long.class, Long.class);
            createCache("C2", String.class, Long.class);
            createCache("C3", String.class, Long.class);
        }};

        Cache c1 = caches.getCache("C1");
        Cache c2 = caches.getCache("C2");
        Cache c3 = caches.getCache("C3");

        c1.put(0L, 1L);
        c2.put("test", 1L);
        c3.put("test", 1L);

        caches.cleanup(Arrays.<Importer>asList(new TestImporter2(), new TestImporter()));

        assertEquals(1, c1.size());
        assertEquals(1, c2.size());
        assertEquals(1, c3.size());

        caches.cleanup(Arrays.<Importer>asList(new TestImporter()));

        assertEquals(1, c1.size());
        assertEquals(1, c2.size());
        assertEquals(0, c3.size());

        caches.cleanup((Collections.<Importer>emptySet()));

        //no need to cleanup
        assertEquals(1, c1.size());
        assertEquals(1, c2.size());
        assertEquals(0, c3.size());

        caches.destroy();
    }

    private class TestImporter extends TabularImporter<Object> {

        @InjectCache(name = "C1")
        private Cache<Long, Long> candidates;

        @InjectCache(name = "C2")
        private Cache<String, Long> companies;

        @Override
        public Data inputData() {
            throw new UnsupportedOperationException();
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

        @InjectCache(name = "C3")
        private Cache<Long, Long> candidates;

        @Override
        public Data inputData() {
            throw new UnsupportedOperationException();
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
