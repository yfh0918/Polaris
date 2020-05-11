package com.polaris.container.gateway;

import com.polaris.container.gateway.pojo.HttpFilterFile;

public interface HttpFilterFileListener {
	default void onChange(HttpFilterFile file) {};
}
