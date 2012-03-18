package com.octo.ebench;

import java.util.Map;

/**
 * Base class for runners
 * @author tvial
 *
 */
public abstract class BenchRunner {
	/**
	 * Subclasses get initialized through this method. They receive initialization parameters, and statistics counters that
	 * they will have to update
	 * @param params
	 * @param stats
	 */
	public abstract void init(Map<String, Integer> params, Statistics stats);
	
	/**
	 * Called when the test rig requests that an event be fed
	 */
	public abstract void feedEvent();
	
	/**
	 * Convenience method to get the value of a parameter
	 * Its real interest is throwing an exception with a friendly message when the parameter was not specified
	 * @param params
	 * @param name
	 * @return
	 */
	public int getParam(Map<String, Integer> params, String name) {
		Integer val = params.get(name);
		if (val == null) {
			throw new RuntimeException(getClass().getSimpleName() + " expects parameter '" + name + "'");
		}
		
		return val;
	}
}
