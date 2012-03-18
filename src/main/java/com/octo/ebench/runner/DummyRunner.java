package com.octo.ebench.runner;

import java.util.Map;

import com.octo.ebench.BenchRunner;
import com.octo.ebench.Statistics;

/**
 * Simple dummy runner used to test the rig
 * Latency is simply picked randomly
 * @author tvial
 *
 */
public class DummyRunner extends BenchRunner {
	private int maxLatency;
	private Statistics statistics;
	
	@Override
	public void init(Map<String, Integer> params, Statistics stats) {
		maxLatency = getParam(params, "maxLatency");
		statistics = stats;
	}
	
	@Override
	public void feedEvent() {
		int lat = (int)(maxLatency * Math.random());
		statistics.addPoint(lat);
	}
}
