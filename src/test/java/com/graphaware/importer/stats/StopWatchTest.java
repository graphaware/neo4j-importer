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

package com.graphaware.importer.stats;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *  Unit test for {@link StopWatch}
 */
public class StopWatchTest {

    @Test
    public void shouldCorrectlyMeasureTime() {
        long start = System.currentTimeMillis();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        long expected = (System.currentTimeMillis() - start) / 1000;
        long actual = stopWatch.getElapsedTimeSecs();

        assertEquals(expected, actual);
    }
}
