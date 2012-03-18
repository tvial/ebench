package com.octo.ebench;

import org.junit.Assert;
import org.junit.Test;

public class TestStatistics {
	@Test
	public void histogramAndAverage() {
		Statistics stats = new Statistics(100);
		
		stats.addPoint(10);
		stats.addPoint(15);
		stats.addPoint(10);
		stats.addPoint(5);
		stats.addPoint(15);
		
		Assert.assertEquals(5, stats.getMinLatency());
		Assert.assertEquals(15, stats.getMaxLatency());
		
		Assert.assertEquals(1l, stats.getPoint(5));
		Assert.assertEquals(2l, stats.getPoint(10));
		Assert.assertEquals(2l, stats.getPoint(15));
		
		Assert.assertEquals(11.0, stats.getAvgLatency());
	}
}
