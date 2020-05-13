package com.polaris.container.gateway;

import com.polaris.container.gateway.pojo.HttpFile;

public interface HttpFileListener {
	default void onChange(HttpFile file) {};
}
