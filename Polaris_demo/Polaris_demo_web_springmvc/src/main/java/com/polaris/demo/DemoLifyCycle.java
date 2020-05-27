package com.polaris.demo;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.polaris.core.component.AbstractLifeCycle;

@Component
public class DemoLifyCycle extends AbstractLifeCycle {
	
	@PostConstruct
	public void initial() {
		start();
	}
	
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
