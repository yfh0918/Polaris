package com.polaris.core.notify;

import java.util.EventObject;

import com.polaris.core.util.SystemClock;

public abstract class Event extends EventObject {

    private static final long serialVersionUID = 1L;
    
    /** System time when the event happened. */
    private final long timestamp;
    
    public Event(Object source) {
        super(source);
        this.timestamp = SystemClock.now();
    }

    /**
     * Return the system time in milliseconds when the event happened.
     */
    public final long getTimestamp() {
        return this.timestamp;
    }
}
