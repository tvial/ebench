package com.octo.ebench.event;

/**
 * Base event class, recording the time when the event was fed into an engine
 * @author tvial
 *
 */
public class BaseEvent {
	private long feedingTime;

	public long getFeedingTime() {
		return feedingTime;
	}

	public void setFeedingTime(long feedingTime) {
		this.feedingTime = feedingTime;
	}
}
