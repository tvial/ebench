package com.octo.ebench;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main class
 * @author tvial
 *
 */
public class EBench {
	/**
	 * Parses parameter specifications such as param=value, or param=min..max,step
	 */
	private final static Pattern PARAM_SPEC = Pattern.compile("^(\\w+)=(\\d+)(\\.\\.(\\d+),(\\d+))?$");
	
	/**
	 * Base package for runner classes (must not be specified on the command line)
	 */
	private final static String RUNNER_PACKAGE = "com.octo.ebench.runner";
	
	/**
	 * Runs a series of tests
	 * 
	 * When a variable parameter is specified, the results are a list of param_value;latency;throughput triplets in CSV format
	 * The outer loop deals with the variable parameter, while the inner loop is a simple batch of event feeding
	 * 
	 * When no variable parameter is specified, a single run occurs and the histogram + overall latency and throughput are printed
	 * @param args
	 */
	public void run(String[] args, boolean showStats) {
		// Parse command line, and prepare parameter list
		RunConfiguration config = parseArgs(args);
		Map<String, Integer> parameters = new HashMap<String, Integer>();
		
		// Create statistics container
		Statistics stats = new Statistics(config.getMaxExpectedLatency());
		
		// Show run configuration
		if (showStats) {
			System.out.println("# " + Arrays.toString(args));
		}
		
		try {
			// Instantiate runner
			BenchRunner runner = (BenchRunner)Class.forName(RUNNER_PACKAGE + "." + config.getRunnerClassName()).newInstance();
			
			// Loop over variable parameter (in the case of a single iteration, this is a dummy parameter)
			//for (int var = config.getVariableParamMin(); var <= config.getVariableParamMax(); var += config.getVariableParamStep()) {
			for (int var = config.getVariableParamMin(); var <= config.getVariableParamMax(); var += config.getVariableParamStep()) {
				// (Re)init runner with effective parameters, i.e. those from the RunConfiguration + the variable one
				parameters.clear();
				parameters.putAll(config.getParameters());
				parameters.put(config.getVariableParamName(), var);
				runner.init(parameters, stats);
				
				// Warm up runner, just in case
				for (int i = 0; i < 1000; i++) {
					runner.feedEvent();
				}
				stats.reset();
				
				// Proceed with main event feeding loop, and measure throughput
				stats.beginFeeding();
				for (int i = 0; i < config.getTotalEvents(); i++) {
					stats.feedEvent();
					runner.feedEvent();
				}
				stats.endFeeding();
				
				// Show statistics
				if (showStats) {
					if (RunConfiguration.NO_VARIABLE_PARAM.equals(config.getVariableParamName())) {
						// No variable parameter -> detailed statistics
						stats.printHistogram();
						System.out.println();
						System.out.println("Average latency = " + stats.getAvgLatency() + " µs");
						System.out.println("Throughput      = " + stats.getThroughput() + " events/s");
					}
					else {
						// Variable parameter -> plot average latency and throughput for current value
						if (var == config.getVariableParamMin()) {
							// Header
							System.out.println(config.getVariableParamName() + ";latency (µs);throughput (ev/s)");
						}
						System.out.println(var + ";" + stats.getAvgLatency() + ";" + stats.getThroughput());
					}
				}	
			}
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Parses the command line. The expected set of arguments is:
	 *    RunnerClass NoOfEventsInInnerLoop ParameterSpecifications*
	 * The last variable parameter specification wins
	 * 
	 * Some environment variables can also be used:
	 *    maxExpectedLatency (default 100000)
	 * @param args
	 * @return
	 */
	public RunConfiguration parseArgs(String[] args) {
		if (args.length < 2) {
			throw new RuntimeException("Expected at least 2 arguments");
		}
		
		RunConfiguration config = new RunConfiguration();
		config.setRunnerClassName(args[0]);
		config.setTotalEvents(Integer.parseInt(args[1]));
		
		for (int i = 2; i < args.length; i++) {
			Matcher m = PARAM_SPEC.matcher(args[i]);
			if (!m.matches()) {
				throw new RuntimeException("Cannot parse parameter specification '" + args[i] + "'");
			}
			
			String name = m.group(1);
			String min = m.group(2);
			String max = m.group(4);
			String step = m.group(5);
			
			if (max == null) {
				// Fixed parameter
				config.getParameters().put(name, Integer.parseInt(min));
			}
			else {
				// Variable parameter
				config.setVariableParamName(name);
				config.setVariableParamMin(Integer.parseInt(min));
				config.setVariableParamMax(Integer.parseInt(max));
				config.setVariableParamStep(Integer.parseInt(step));
			}
		}
		
		// Environment variables
		String maxExpLatency = System.getProperty("maxExpectedLatency");
		if (maxExpLatency == null) {
			config.setMaxExpectedLatency(100000);
		}
		else {
			config.setMaxExpectedLatency(Integer.parseInt(maxExpLatency));
		}
		
		return config;
	}
	
	/**
	 * Main entry point
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			EBench e = new EBench();
			// Perform a few blind runs, to ensure everything is in a steady state when we output statistics
			for (int i = 0; i < 3; i++) {
				e.run(args, i == 2);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
