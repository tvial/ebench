package com.octo.ebench.runner;

import java.util.Map;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * This runner creates lots of statements running in parallel, feeding on the same event stream
 * 
 * The interesting variable parameter is the number of statements (nStatements)
 * Another parameter is the listener attach mode:
 *    - LSTN_MAP_FIRST (0) means a single listener attached to the first created statement
 *    - LSTN_MAP_LAST  (1) means a single listener attached to the last created statement
 *    - LSTN_MAP_ALL   (2) means all statements have listeners and gather statistics
 * @author tvial
 *
 */
public class ParallelStatements extends EsperRunner {
	private final static int LSTN_MAP_FIRST = 0;
	private final static int LSTN_MAP_LAST = 1;
	private final static int LSTN_MAP_ALL = 2;
	
	private int nStatements;
	private int listenerAttach;
	
	@Override
	public void init(Map<String, Integer> parameters) {
		nStatements = getParam(parameters, "nStatements");
		listenerAttach = getParam(parameters, "listenerAttach");
	}

	@Override
	public void createStatements(EPAdministrator epAdmin) {
		for (int i = 0; i < nStatements; i++) {
			EPStatement stmt = epAdmin.createEPL("select feedingTime, payload from EventWithPayload");
			
			boolean attach = false;
			switch(listenerAttach) {
			case LSTN_MAP_FIRST:
				attach = (i == 0);
				break;
			case LSTN_MAP_LAST:
				attach = (i == nStatements - 1);
				break;
			case LSTN_MAP_ALL:
				attach = true;
				break;
			}
			
			if (attach) {
				stmt.addListener(new StatUpdateListener());
			}
		}
	}
}
