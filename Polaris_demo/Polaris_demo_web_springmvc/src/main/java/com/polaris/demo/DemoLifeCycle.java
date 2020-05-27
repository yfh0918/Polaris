package com.polaris.demo;

import com.polaris.core.component.AbstractLifeCycle;

/**
 * The DemoLifyCycle for generic components.
 * {@link DemoApplication}
 */
public class DemoLifeCycle extends AbstractLifeCycle {
	
	@Override
	public void start() {
		System.out.println("here is add resource");
		super.start();
	}

	@Override
	public void stop() {
		System.out.println("here is release resource");
	}

}
