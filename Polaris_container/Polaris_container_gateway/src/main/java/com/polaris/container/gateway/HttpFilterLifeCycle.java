package com.polaris.container.gateway;

import com.polaris.container.gateway.pojo.HttpFilterEntity;

public interface HttpFilterLifeCycle {

	default void start(HttpFilterEntity httpFilterEntity) {};
	default void stop(HttpFilterEntity httpFilterEntity) {};
}
