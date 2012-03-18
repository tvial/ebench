package com.octo.ebench.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.octo.ebench.BenchRunner;
import com.octo.ebench.Statistics;
import com.octo.ebench.event.BaseEvent;
import com.octo.ebench.event.EventWithPayload;

/**
 * Base class for Esper-based runners
 * Takes care of initialization, and event feeding
 * @author tvial
 *
 */
public abstract class EsperRunner extends BenchRunner {
	/**
	 * Default UpdateListener that automatically updates statistics as output events are captured
	 * @author tvial
	 *
	 */
	public final class StatUpdateListener implements UpdateListener {
		
		public void update(EventBean[] newEvents, EventBean[] oldEvents) {
			long receptionTime = System.nanoTime();
			
			if ((newEvents != null) && (newEvents.length > 0)) {				
				for (EventBean eventBean : newEvents) {
					statistics.addPoint((int)((receptionTime - (Long)eventBean.get("feedingTime")) / 1000));
				}
			}
		}
	}
	
	/**
	 * Payload type: byte array (payloadSize is the array length)
	 */
	private final static int PAYLOAD_BYTE_ARRAY = 0;
	/**
	 * Payload type: nested ArrayList's (payloadSize is the tree depth)
	 */
	private final static int PAYLOAD_NESTED_LISTS = 1;
	
	/**
	 * The Esper provider SPI
	 */
	protected EPServiceProvider epService = null;
	
	/**
	 * Statistic counters
	 */
	protected Statistics statistics;
	
	/**
	 * Payload attached to BaseEvent's
	 * The payload is reused to avoid instantiation overhead that could introduce a bias
	 */
	private Object payload;
	
	/**
	 * Simple parameter initialization for subclasses
	 * @param parameters
	 */
	public abstract void init(Map<String, Integer> parameters);
	
	/**
	 * Subclasses create their statements & listeners here
	 * @param epAdmin
	 */
	public abstract void createStatements(EPAdministrator epAdmin);
	
	@Override
	public void init(Map<String, Integer> parameters, Statistics stats) {
		// Create payload
		payload = createPayload(parameters);
		
		statistics = stats;
		
		// Resets the Esper engine
		if (epService != null) {
			epService.destroy();
		}
		
		Configuration config = new Configuration();
		config.addEventTypeAutoName("com.octo.ebench.event");
		epService = EPServiceProviderManager.getDefaultProvider(config);
		
		init(parameters);
		createStatements(epService.getEPAdministrator());
		
		// Performs a GC collection, just in case a previous run left stuff behind
		System.gc();
	}
	
	/**
	 * Creates the payload prototype. The default version creates an array of bytes but subclasses can customize this
	 * @param parameters
	 * @return
	 */
	protected Object createPayload(Map<String, Integer> parameters) {
		int size = getParam(parameters, "payloadSize");
		int type = getParam(parameters, "payloadType");
		
		switch(type) {
		case PAYLOAD_BYTE_ARRAY:
			return new byte[size];
		case PAYLOAD_NESTED_LISTS:
			Object node = new Object();
			for (int i = 0; i < size; i++) {
				List<Object> newNode = new ArrayList<Object>();
				newNode.add(node);
				node = newNode;
			}
			return node;
		default:
			throw new RuntimeException("Unknown payload type " + type);
		}
	}
	
	@Override
	public void feedEvent() {
		// Simply create a BaseEvent and feed the Esper engine
		BaseEvent event = new EventWithPayload(payload);
		event.setFeedingTime(System.nanoTime());
		
		epService.getEPRuntime().sendEvent(event);
	}
}
