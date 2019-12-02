package com.polaris.config.apollo;

import org.springframework.core.annotation.Order;

import com.polaris.core.config.ConfListener;
import com.polaris.core.config.ConfigHandler;

@Order(1)
public class ConfApolloHandler implements ConfigHandler {

	@Override
	public String getConfig(String fileName, String group) {
		return ConfApolloClient.getInstance().getConfig(fileName,group);
	}

	@Override
	public void addListener(String fileName, String group, ConfListener listener) {
		ConfApolloClient.getInstance().addListener(fileName, group, listener);
	}
}
