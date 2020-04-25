package com.polaris.container.gateway;

import com.polaris.container.gateway.pojo.HttpFilterFile;

public interface HttpFilterLifeCycle {

	default void start() {};
	default void onChange(HttpFilterFile file) {};
	default void stop() {};
}
