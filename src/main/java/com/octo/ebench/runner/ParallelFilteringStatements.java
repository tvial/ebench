package com.octo.ebench.runner;

import java.util.Map;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;

/**
 * This runner creates a fixed number of filtering statements. The interesting variable is the number of statements that will
 * effectively filter out out events, compared to the number of statements that will let them through. All statements have
 * listeners attached to them.
 * 
 * There are two filtering methods:
 *    - FILTER_WHERE: a "WHERE payload is [not] null" clause
 *    - FILTER_TYPE: a "FROM _EventType_" clause
 * @author tvial
 *
 */
public class ParallelFilteringStatements extends EsperRunner {
	private final static int FILTER_WHERE = 0;
	private final static int FILTER_TYPE = 1;
	
	private int nStatements;
	private int nFilteringStatements;
	private int filteringMethod;
	
	@Override
	public void init(Map<String, Integer> parameters) {
		nStatements = getParam(parameters, "nStatements");
		nFilteringStatements = getParam(parameters, "nFilteringStatements");
		filteringMethod = getParam(parameters, "filteringMethod");
	}

	@Override
	public void createStatements(EPAdministrator epAdmin) {
		for (int i = 0; i < nStatements; i++) {
			String epl = "";
			
			if (i < nFilteringStatements) {
				// This statement will filter out
				switch (filteringMethod) {
				case FILTER_WHERE:
					epl = "select feedingTime from EventWithPayload where payload is null";
					break;
				case FILTER_TYPE:
					epl = "select feedingTime from OtherEvent";
					break;
				}
			}
			else {
				// This statement will let through
				switch (filteringMethod) {
				case FILTER_WHERE:
					epl = "select feedingTime from EventWithPayload where payload is not null";
					break;
				case FILTER_TYPE:
					epl = "select feedingTime from EventWithPayload";
					break;
				}	
			}
			
			epAdmin.createEPL(epl)
				.addListener(new StatUpdateListener());
		}
	}
}
