package com.polaris.config.file;

import com.polaris.core.config.ConfListener;
import com.polaris.core.config.ConfigHandler;

public class ConfFileHandler implements ConfigHandler {

	@Override
	public String getConfig(String fileName, String group) {
		return ConfFileClient.getInstance().getConfig(fileName,group);
	}

	@Override
	public void addListener(String fileName, String group, ConfListener listener) {
		ConfFileClient.getInstance().addListener(fileName, group, listener);
	}
}
