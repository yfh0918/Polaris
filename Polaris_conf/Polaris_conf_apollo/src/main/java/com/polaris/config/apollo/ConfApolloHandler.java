package com.polaris.config.apollo;

import org.springframework.core.annotation.Order;

import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.ConfHandlerOrder;
import com.polaris.core.config.ConfHandler;

@Order(ConfHandlerOrder.APOLLO)
public class ConfApolloHandler implements ConfHandler {

	@Override
	public String get(String fileName, String group) {
		return ConfApolloClient.getInstance().getConfig(fileName,group);
	}

	@Override
	public void listen(String fileName, String group, ConfHandlerListener listener) {
		ConfApolloClient.getInstance().addListener(fileName, group, listener);
	}
}
