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
    private static final CopyOnWriteArrayList<AbstractManagedLifeCycle> _lifeCycles = new CopyOnWriteArrayList<>();

    public static void register(AbstractManagedLifeCycle lifeCycle) {
    	if (_lifeCycles.contains(lifeCycle)) {
    		return;
    	}
    	logger.debug("LifeCycleManager add lifeCycle:{}",lifeCycle.getClass().getName());
    	_lifeCycles.add(lifeCycle);
	}
	
	public static void start() {
		Iterator<AbstractManagedLifeCycle> iterator = _lifeCycles.iterator();
		while (iterator.hasNext()) {
			AbstractManagedLifeCycle lifeCycle = iterator.next();
			logger.debug("LifeCycleManager start lifeCycle:{}",lifeCycle.getClass().getName());
			lifeCycle.start();
		}
	}
	
	public static void stop() {
		Iterator<AbstractManagedLifeCycle> iterator = _lifeCycles.iterator();
		while (iterator.hasNext()) {
			AbstractManagedLifeCycle lifeCycle = iterator.next();
	    	logger.debug("LifeCycleManager stop lifeCycle:{}",lifeCycle.getClass().getName());
			lifeCycle.stop();
		}
	}

    public static void unRegister(AbstractManagedLifeCycle lifeCycle) {
    	logger.debug("LifeCycleManager remove lifeCycle:{}",lifeCycle.getClass().getName());
    	_lifeCycles.remove(lifeCycle);
	}
    
}
