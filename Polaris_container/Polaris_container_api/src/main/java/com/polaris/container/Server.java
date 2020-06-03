package com.polaris.container;

import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.core.component.LifeCycle;

public interface Server extends LifeCycle{
	default ConfigurableApplicationContext getContext() {return null;}
}
