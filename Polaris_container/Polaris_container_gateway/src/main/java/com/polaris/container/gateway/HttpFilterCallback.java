package com.polaris.container.gateway;

import com.polaris.container.gateway.pojo.HttpFilterFile;

public interface HttpFilterCallback {
	default void onChange(HttpFilterFile file) {};
}
