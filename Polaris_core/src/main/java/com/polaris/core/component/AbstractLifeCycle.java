package com.polaris.core.component;

public abstract class AbstractLifeCycle implements LifeCycle{
	@Override
	public void start() {
		LifeCycleManager.register(this);
	}
}
