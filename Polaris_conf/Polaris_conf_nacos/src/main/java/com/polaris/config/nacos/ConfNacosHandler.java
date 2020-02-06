package com.polaris.config.nacos;

import org.springframework.core.annotation.Order;

import com.polaris.core.config.ConfListener;
import com.polaris.core.config.ConfHandlerOrder;
import com.polaris.core.config.ConfHandler;

@Order(ConfHandlerOrder.NACOS)
public class ConfNacosHandler implements ConfHandler {

	@Override
	public String getConfig(String fileName, String group) {
		return ConfNacosClient.getInstance().getConfig(fileName,group);
	}

	@Override
	public void addListener(String fileName, String group, ConfListener listener) {
		ConfNacosClient.getInstance().addListener(fileName, group, listener);
	}
}
