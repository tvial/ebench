package com.octo.ebench.runner;

import java.util.Map;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * This runner creates lots of statements chained together through INSERT INTO ... SELECT FROM ... clauses
 * Events flow as follows:
 *    BaseEvent -> stream_0 -> stream_1 -> stream_2 -> ... -> stream_N -> listener
 *    
 * The interesting variable parameter here is the number of statements (nStatements)
 * @author tvial
 *
 */
public class ChainedStatements extends EsperRunner {
	
	private int nStatements;
	
	@Override
	public void init(Map<String, Integer> parameters) {
		nStatements = getParam(parameters, "nStatements");
	}

	@Override
	public void createStatements(EPAdministrator epAdmin) {
		for (int i = 0; i < nStatements; i++) {
			// What stream to forward to
			String into = "stream_" + i;
			// What stream to read from - either the raw event type, or the previous stream
			String from = (i == 0) ? "com.octo.ebench.event.EventWithPayload" : ("stream_" + (i - 1));
			
			epAdmin.createEPL("insert into " + into + " select feedingTime, payload from " + from);
		}
		
		// Last statement gets the listener
		epAdmin.createEPL("select feedingTime, payload from stream_" + (nStatements - 1))
			.addListener(new StatUpdateListener());
	}
}
