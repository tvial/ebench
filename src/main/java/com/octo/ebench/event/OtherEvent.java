package com.octo.ebench.event;

/**
 * Some other event type
 * @author tvial
 *
 */
public class OtherEvent extends BaseEvent {
	private String someField;

	public String getSomeField() {
		return someField;
	}

	public void setSomeField(String someField) {
		this.someField = someField;
	}
}
