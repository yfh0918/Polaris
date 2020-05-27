package com.polaris.core.component;

import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The LifeCycleManager for generic components.
 * {@link LifeCycleListener}
 */
public class LifeCycleManager {
	private static final Logger logger = LoggerFactory.getLogger(LifeCycleManager.class);
    private static final CopyOnWriteArrayList<LifeCycle> _lifeCycles = new CopyOnWriteArrayList<LifeCycle>();

    public static void register(LifeCycle lifeCycle) {
    	logger.info("LifeCycleManager add lifeCycle:{}",lifeCycle.getClass().getName());
    	_lifeCycles.add(lifeCycle);
	}
	
	public static void close() {
		for (LifeCycle lifeCycle : _lifeCycles) {
	    	logger.info("LifeCycleManager stop lifeCycle:{}",lifeCycle.getClass().getName());
			lifeCycle.stop();
		}
		_lifeCycles.clear();
	}
}
