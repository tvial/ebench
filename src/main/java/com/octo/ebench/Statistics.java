package com.octo.ebench;

import java.util.Arrays;

/**
 * Gathers runtime statistics when events are fed to runners
 * 
 * Latency: the time elapsed between event feeding and capturing - measured in microseconds
 * Throughput: the rate at which the system can feed events - measured in events/second
 * 
 * TODO: gather statistics on memory consumption
 * @author tvial
 *
 */
public class Statistics {
	
	/**
	 * Histogram of repartition of latencies
	 * Each value histogram[lat] holds the number of samples with measured latency lat
	 */
	private long[] histogram;
	
	/**
	 * Minimum observed latency
	 */
	private int minLatency;
	
	/**
	 * Maximum observed latency
	 */
	private int maxLatency;
	
	/**
	 * Total number of events fed into the system
	 * This can be inferior to the number of samples in histogram (e.g. when a single fed event leads to several captured events)
	 * This is used for throughput calculation
	 */
	private long totalFedEvents;
	
	/**
	 * Time when event feeding began
	 */
	private long beginFeedTime;
	
	/**
	 * Time when event feeding was stopped
	 */
	private long endFeedTime;
	
	public Statistics(int maxExpectedLatency) {
		histogram = new long[maxExpectedLatency];
		reset();
	}
	
	/**
	 * Resets the statistics
	 * Can be called between runs
	 */
	public void reset() {
		minLatency = maxLatency = -1;
		Arrays.fill(histogram, 0l);
		
		beginFeedTime = endFeedTime = -1l;
		totalFedEvents = 0l;
	}
	
	/**
	 * Notifies the feeding of an event
	 */
	public void feedEvent() {
		totalFedEvents++;
	}
	
	/**
	 * Adds a sample with observed latency to the histogram
	 * Beware that synchronization doesn't incur overhead and skews the final results
	 * @param latency
	 */
	public synchronized void addPoint(int latency) {
		if ((latency < minLatency) || (minLatency < 0)) {
			minLatency = latency;
		}
		if ((latency > maxLatency) || (maxLatency < 0)) {
			maxLatency = latency;
		}
		
		histogram[latency]++;
	}
	
	/**
	 * Returns the number of samples for a given latency, from the histogram
	 * @param latency
	 * @return
	 */
	public long getPoint(int latency) {
		return histogram[latency];
	}
	
	/**
	 * Gets the average latency
	 * @return
	 */
	public double getAvgLatency() {
		long sum = 0l;
		long n = 0l;
		
		if ((minLatency < 0) && (maxLatency < 0)) {
			return -1.;
		}
		
		for (int lat = minLatency; lat <= maxLatency; lat++) {
			sum += lat * histogram[lat];
			n += histogram[lat];
		}
		
		return (double)sum / (double)n;
	}

	public int getMinLatency() {
		return minLatency;
	}

	public int getMaxLatency() {
		return maxLatency;
	}
	
	/**
	 * Prints the histogram in CSV format
	 */
	public void printHistogram() {
		System.out.println("latency (µs);n");
		for (int lat = minLatency; lat <= maxLatency; lat++) {
			if (histogram[lat] > 0) {
				System.out.println(lat + ";" + histogram[lat]);
			}
		}
	}
	
	/**
	 * Called when feeding begins
	 */
	public void beginFeeding() {
		totalFedEvents = 0l;
		endFeedTime = -1l;
		beginFeedTime = System.nanoTime();
	}
	
	/**
	 * Called when feeding ends
	 */
	public void endFeeding() {
		endFeedTime = System.nanoTime();
	}
	
	/**
	 * Calculates the observed throughput
	 * @return
	 */
	public double getThroughput() {
		if ((beginFeedTime == -1) || (endFeedTime == -1)) {
			throw new RuntimeException("No measure or still in progress");
		}
		
		return 1000000000. * (double)totalFedEvents / (double)(endFeedTime - beginFeedTime);
	}
}
