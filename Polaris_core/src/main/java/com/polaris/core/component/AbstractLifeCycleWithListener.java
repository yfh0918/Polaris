package com.polaris.core.component;

/**
 * The AbstractLifeCycle for generic components.
 */
public abstract class AbstractLifeCycleWithListener extends AbstractLifeCycle implements LifeCycleListener{
	public AbstractLifeCycleWithListener() {
        addLifeCycleListener(this);
    }
	
	@Override
    public void lifeCycleFailure(LifeCycle event, Throwable cause) {
    }

    @Override
    public void lifeCycleStarted(LifeCycle event) {
    }

    @Override
    public void lifeCycleStarting(LifeCycle event) {
    }

    @Override
    public void lifeCycleStopped(LifeCycle event) {
    }

    @Override
    public void lifeCycleStopping(LifeCycle event) {
    }
}
