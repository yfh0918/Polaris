package com.polaris.config.nacos;

import org.springframework.core.annotation.Order;

import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.ConfHandlerOrder;
import com.polaris.core.config.ConfHandler;

@Order(ConfHandlerOrder.NACOS)
public class ConfNacosHandler implements ConfHandler {

	@Override
	public String get(String group, String fileName) {
		return ConfNacosClient.getInstance().getConfig(group, fileName);
	}

	@Override
	public void listen(String group, String fileName, ConfHandlerListener listener) {
		ConfNacosClient.getInstance().addListener(group, fileName, listener);
	}
}
