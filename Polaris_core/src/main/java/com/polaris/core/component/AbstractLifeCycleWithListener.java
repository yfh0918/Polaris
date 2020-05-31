package com.polaris.core.component;

/**
 * The AbstractLifeCycle for generic components.
 */
public abstract class AbstractLifeCycleWithListener extends AbstractLifeCycle implements LifeCycleListener{
	protected AbstractLifeCycleWithListener() {
        addLifeCycleListener(this);
    }
}
