package com.polaris.config.apollo;

import org.springframework.core.annotation.Order;

import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.ConfHandlerOrder;
import com.polaris.core.config.ConfHandler;

@Order(ConfHandlerOrder.APOLLO)
public class ConfApolloHandler implements ConfHandler {

	@Override
	public String get(String group,String fileName) {
		return ConfApolloClient.getInstance().getConfig(group,fileName);
	}

	@Override
	public void listen(String group,String fileName, ConfHandlerListener listener) {
		ConfApolloClient.getInstance().addListener(group,fileName, listener);
	}
}
