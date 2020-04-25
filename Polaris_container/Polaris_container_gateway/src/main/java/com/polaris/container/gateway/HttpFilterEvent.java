package com.polaris.container.gateway;

import com.polaris.container.gateway.pojo.HttpFilterFile;

public interface HttpFilterEvent {
	default void onChange(HttpFilterFile file) {};
}
