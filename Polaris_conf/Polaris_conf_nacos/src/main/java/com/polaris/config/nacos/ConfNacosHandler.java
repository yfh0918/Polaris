package com.polaris.config.nacos;

import java.util.List;

import com.polaris.comm.config.ConfListener;
import com.polaris.comm.config.ConfigHandler;

public class ConfNacosHandler implements ConfigHandler {

	@Override
	public String getValue(String key, String fileName, boolean isWatch) {
		return ConfNacosClient.getInstance().getConfig(key, fileName);
	}
	
	@Override
	public List<String> getAllPropertyFiles() {
		return ConfNacosClient.getInstance().getAllPropertyFiles();
	}
	
	@Override
	public void addListener(String fileName, ConfListener listener) {
		ConfNacosClient.getInstance().addListener(fileName, listener);
	}
//	
//	@Override
//	public String getFileContent(String fileName) {
//		return ConfNacosClient.getInstance().getFileContent(fileName);
//	}
}
