package com.polaris.core.component;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The LifeCycleManager for generic components.
 * {@link LifeCycleListener}
 */
public class LifeCycleManager {
	private static final Logger logger = LoggerFactory.getLogger(LifeCycleManager.class);
    private static final CopyOnWriteArrayList<AbstractLifeCycle> _lifeCycles = new CopyOnWriteArrayList<>();

    public static void register(AbstractLifeCycle lifeCycle) {
    	logger.info("LifeCycleManager add lifeCycle:{}",lifeCycle.getClass().getName());
    	_lifeCycles.add(lifeCycle);
	}
	
	public static void start() {
		Iterator<AbstractLifeCycle> iterator = _lifeCycles.iterator();
		while (iterator.hasNext()) {
			AbstractLifeCycle lifeCycle = iterator.next();
			logger.info("LifeCycleManager start lifeCycle:{}",lifeCycle.getClass().getName());
			lifeCycle.start();
		}
	}
	
	public static void stop() {
		Iterator<AbstractLifeCycle> iterator = _lifeCycles.iterator();
		while (iterator.hasNext()) {
			AbstractLifeCycle lifeCycle = iterator.next();
	    	logger.info("LifeCycleManager stop lifeCycle:{}",lifeCycle.getClass().getName());
			lifeCycle.stop();
		}
	}

    public static void unRegister(AbstractLifeCycle lifeCycle) {
    	logger.info("LifeCycleManager remove lifeCycle:{}",lifeCycle.getClass().getName());
    	_lifeCycles.remove(lifeCycle);
	}
}
