package com.graphaware.importer.stats;

public class StopWatch {

	private long startTime = 0;

	public void start() {
		this.startTime = System.currentTimeMillis();
	}

	public long getElapsedTimeSecs() {
        return ((System.currentTimeMillis() - startTime) / 1000);
	}
}
