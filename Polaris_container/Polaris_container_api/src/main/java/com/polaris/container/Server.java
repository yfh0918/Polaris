package com.polaris.container;

import com.polaris.core.component.LifeCycle;

public interface Server extends LifeCycle{
	default Object getContext() {return null;}
}
