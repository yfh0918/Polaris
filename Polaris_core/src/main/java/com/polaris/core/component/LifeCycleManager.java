package com.polaris.core.component;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The LifeCycleManager for generic components.
 * {@link LifeCycleRegisterServerListener}
 */
public class LifeCycleManager {
    private static final CopyOnWriteArrayList<LifeCycle> _lifeCycles = new CopyOnWriteArrayList<LifeCycle>();

    public static void register(LifeCycle lifeCycle) {
    	_lifeCycles.add(lifeCycle);
	}
	
	public static void close() {
		for (LifeCycle lifeCycle : _lifeCycles) {
			lifeCycle.stop();
		}
		_lifeCycles.clear();
	}
}
