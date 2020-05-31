package com.polaris.core.component;

public interface LifeCycleState {
	public static final String STOPPED = "STOPPED";
    public static final String FAILED = "FAILED";
    public static final String STARTING = "STARTING";
    public static final String STARTED = "STARTED";
    public static final String STOPPING = "STOPPING";
    public static final String RUNNING = "RUNNING";
    
    public static final int STATE_FAILED = -1;
    public static final int STATE_STOPPED = 0;
    public static final int STATE_STARTING = 1;
    public static final int STATE_STARTED = 2;
    public static final int STATE_STOPPING = 3;
}
