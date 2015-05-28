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
