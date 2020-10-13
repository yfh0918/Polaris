package com.polaris.config.file;

import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.ConfHandlerOrder;

import org.springframework.core.annotation.Order;

import com.polaris.core.config.ConfHandler;

@Order(ConfHandlerOrder.FILE)
public class ConfFileHandler implements ConfHandler {

	@Override
	public String get(String group,String fileName) {
		return ConfFileClient.getInstance().getConfig(group,fileName);
	}

	@Override
	public void listen(String group,String fileName, ConfHandlerListener listener) {
		ConfFileClient.getInstance().addListener(group, fileName, listener);
	}
}
