package com.polaris.container.gateway;

public interface HttpFilterLifeCycle {
	default void start() {};
	default void stop() {};
}
