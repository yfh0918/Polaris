package com.polaris.demo;

import com.polaris.core.component.AbstractLifeCycle;

/**
 * The DemoLifyCycle for generic components.
 * {@link DemoController}
 */
public class DemoLifeCycle extends AbstractLifeCycle {

	@Override
	public void doStart() throws Exception {
		System.out.println("here is add resource");
	}

	@Override
	public void doStop() {
		System.out.println("here is release resource");
	}
}
