package com.octo.ebench;

import org.junit.Assert;
import org.junit.Test;

public class TestEBench {
	
	private void assertNoVariableParameter(RunConfiguration config) {
		Assert.assertEquals(RunConfiguration.NO_VARIABLE_PARAM, config.getVariableParamName());
		Assert.assertEquals(0, config.getVariableParamMin());
		Assert.assertEquals(0, config.getVariableParamMax());
		Assert.assertEquals(1, config.getVariableParamStep());
	}
	
	@Test
	public void parseArgsWithNoParameters() {
		String[] args = new String[] {
				"SomeRunner",
				"1000"
		};
		
		RunConfiguration config = new EBench().parseArgs(args);
		
		Assert.assertEquals("SomeRunner", config.getRunnerClassName());
		Assert.assertEquals(1000, config.getTotalEvents());
		Assert.assertEquals(0, config.getParameters().size());
		assertNoVariableParameter(config);
	}
	
	@Test
	public void parseArgsWithParameters() {
		String[] args = new String[] {
				"SomeRunner",
				"1000",
				"param=5"
		};
		
		RunConfiguration config = new EBench().parseArgs(args);
		
		Assert.assertEquals("SomeRunner", config.getRunnerClassName());
		Assert.assertEquals(1000, config.getTotalEvents());
		Assert.assertEquals(1, config.getParameters().size());
		Assert.assertEquals(5, config.getParameters().get("param"));
		assertNoVariableParameter(config);
	}
	
	@Test
	public void parseArgsWithVariableParameter() {
		String[] args = new String[] {
				"SomeRunner",
				"1000",
				"param=5",
				"varParam=10..2300,8"
		};
		
		RunConfiguration config = new EBench().parseArgs(args);
		
		Assert.assertEquals("SomeRunner", config.getRunnerClassName());
		Assert.assertEquals(1000, config.getTotalEvents());
		Assert.assertEquals(1, config.getParameters().size());
		Assert.assertEquals(5, config.getParameters().get("param"));
		
		Assert.assertEquals("varParam", config.getVariableParamName());
		Assert.assertEquals(10, config.getVariableParamMin());
		Assert.assertEquals(2300, config.getVariableParamMax());
		Assert.assertEquals(8, config.getVariableParamStep());
	}
}
