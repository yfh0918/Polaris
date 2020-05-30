package com.polaris.core.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The AbstractLifeCycle for generic components.
 * {@link LifeCycleManager}
 */
public abstract class AbstractManagedLifeCycle extends AbstractLifeCycleWithListener{
	final static Logger logger = LoggerFactory.getLogger(AbstractManagedLifeCycle.class);
	
	@Override
	public void lifeCycleStarted(LifeCycle event) {
		if (event instanceof AbstractManagedLifeCycle) {
			LifeCycleManager.register((AbstractManagedLifeCycle)event);
		}
	}

	@Override
	public void lifeCycleStopped(LifeCycle event) {
		if (event instanceof AbstractManagedLifeCycle) {
			LifeCycleManager.unRegister((AbstractManagedLifeCycle)event);
		}
	}
}