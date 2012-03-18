package com.octo.ebench.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * This runner runs a single EPL statement feeding on the BaseEvent stream
 * The interesting variations are on the payload size/structure, with the payloadType and payloadSize parameters
 * @author tvial
 *
 */
public class SingleStatement extends EsperRunner {
	
	@Override
	public void init(Map<String, Integer> parameters) {
		
	}

	@Override
	public void createStatements(EPAdministrator epAdmin) {
		epAdmin.createEPL("select feedingTime, payload from EventWithPayload")
			.addListener(new StatUpdateListener());
	}
}
