package com.octo.ebench.event;

/**
 * An event with a payload
 * Can be used to test how the payload (size, structure) impacts performance
 * @author tvial
 *
 */
public class EventWithPayload extends BaseEvent {
	private Object payload;
	
	public EventWithPayload(Object payload) {
		this.payload = payload;
	}
	
	public Object getPayload() {
		return payload;
	}
}
