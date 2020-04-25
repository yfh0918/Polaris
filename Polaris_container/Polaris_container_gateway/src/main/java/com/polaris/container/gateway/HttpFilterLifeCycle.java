package com.polaris.container.gateway;

import com.polaris.container.gateway.pojo.FileType;

public interface HttpFilterLifeCycle {

	default void start() {};
	default void onChange(FileType fileType) {};
	default void stop() {};
}
