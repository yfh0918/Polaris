package com.polaris.config.file;

import com.polaris.core.config.ConfListener;
import com.polaris.core.config.ConfHandlerOrder;

import org.springframework.core.annotation.Order;

import com.polaris.core.config.ConfHandler;

@Order(ConfHandlerOrder.FILE)
public class ConfFileHandler implements ConfHandler {

	@Override
	public String get(String fileName, String group) {
		return ConfFileClient.getInstance().getConfig(fileName,group);
	}

	@Override
	public void listen(String fileName, String group, ConfListener listener) {
		ConfFileClient.getInstance().addListener(fileName, group, listener);
	}
}
