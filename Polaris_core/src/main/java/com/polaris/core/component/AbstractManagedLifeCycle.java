package com.polaris.core.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The AbstractLifeCycle for generic components.
 * {@link LifeCycleManager}
 */
public abstract class AbstractManagedLifeCycle extends AbstractLifeCycle implements LifeCycle.Listener{
	final static Logger logger = LoggerFactory.getLogger(AbstractManagedLifeCycle.class);
	
	public AbstractManagedLifeCycle() {
        addLifeCycleListener(this);
    }

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

	@Override
	public void lifeCycleFailure(LifeCycle event, Throwable cause) {
		if (event instanceof AbstractManagedLifeCycle) {
			logger.error("failed AbstractManagedLifeCycle: " + cause, cause);
		}
	}

	@Override
	public void lifeCycleStarting(LifeCycle event) {
	}

	@Override
	public void lifeCycleStopping(LifeCycle event) {
	}
}