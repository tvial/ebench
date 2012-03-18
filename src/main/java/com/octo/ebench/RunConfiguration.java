package com.octo.ebench;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration parameters for a test run
 * @author tvial
 *
 */
public class RunConfiguration {
	/**
	 * Special value for variableParamName, indicating that no variable parameter was specified (single run)
	 */
	public final static String NO_VARIABLE_PARAM = "_noVarParam";
	
	/**
	 * Maximum expected latency (for the histogram)
	 */
	private int maxExpectedLatency;
	
	/**
	 * Runner class name (with package name)
	 */
	private String runnerClassName;
	
	/**
	 * Number of events to feed with every iteration of the loop (i.e. for each value of the variable parameter, inner loop)
	 */
	private int totalEvents;
	
	/**
	 * Parameters for the runner
	 */
	private Map<String, Integer> parameters;
	
	/**
	 * Name of the parameter whose value will change with each iteration (main outer loop)
	 * When the special value NO_VARIABLE_PARAM is specified, the loop is a single iteration (single run)
	 */
	private String variableParamName;
	
	/**
	 * Minimum value of the variable parameter
	 */
	private int variableParamMin;
	
	/**
	 * Maximum value of the variable parameter
	 * The max value is inclusive if (variableParamMax - variableParamMin) is a multiple of variableParamStep
	 */
	private int variableParamMax;
	
	/**
	 * Step incremenet of the variable parameter
	 */
	private int variableParamStep;
	
	/**
	 * Initialization, by default without a variable parameter
	 */
	public RunConfiguration() {
		parameters = new HashMap<String, Integer>();
		variableParamName = NO_VARIABLE_PARAM;
		variableParamMin = 0;
		variableParamMax = 0;
		variableParamStep = 1;
	}
	
	public String getRunnerClassName() {
		return runnerClassName;
	}

	public void setRunnerClassName(String runnerClassName) {
		this.runnerClassName = runnerClassName;
	}

	public int getTotalEvents() {
		return totalEvents;
	}

	public void setTotalEvents(int totalEvents) {
		this.totalEvents = totalEvents;
	}

	public String getVariableParamName() {
		return variableParamName;
	}

	public void setVariableParamName(String variableParamName) {
		this.variableParamName = variableParamName;
	}

	public int getVariableParamMin() {
		return variableParamMin;
	}

	public void setVariableParamMin(int variableParamMin) {
		this.variableParamMin = variableParamMin;
	}

	public int getVariableParamMax() {
		return variableParamMax;
	}

	public void setVariableParamMax(int variableParamMax) {
		this.variableParamMax = variableParamMax;
	}

	public int getVariableParamStep() {
		return variableParamStep;
	}

	public void setVariableParamStep(int variableParamStep) {
		this.variableParamStep = variableParamStep;
	}

	public Map<String, Integer> getParameters() {
		return parameters;
	}

	public int getMaxExpectedLatency() {
		return maxExpectedLatency;
	}

	public void setMaxExpectedLatency(int maxExpectedLatency) {
		this.maxExpectedLatency = maxExpectedLatency;
	}
	
}
