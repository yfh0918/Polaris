package com.polaris.config.apollo;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigFileChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.model.ConfigFileChangeEvent;
import com.github.pagehelper.util.StringUtil;
import com.polaris.core.config.ConfClient;
import com.polaris.core.config.ConfListener;
import com.polaris.core.util.PropertyUtils;

public class ConfApolloClient { 
	
	private static final Logger logger = LoggerFactory.getLogger(ConfApolloClient.class);
	private volatile static ConfApolloClient INSTANCE = new ConfApolloClient();
	public static ConfApolloClient getInstance(){
		return INSTANCE;
	}
	private ConfApolloClient() {
		try {
			String appFile = PropertyUtils.getFilePath("META-INF"+File.separator+"app.properties");
			PropertyUtils.writeData(appFile, "app.id", ConfClient.getAppName(), true);
			PropertyUtils.writeData(appFile, "apollo.meta", ConfClient.getConfigRegistryAddress(), true);
			if (StringUtil.isNotEmpty(ConfClient.getNameSpace())) {
				PropertyUtils.writeData(appFile, "apollo.env", ConfClient.getNameSpace(), true);
			}
			if (StringUtil.isNotEmpty(ConfClient.getGroup())) {
				System.setProperty("apollo.cluster", ConfClient.getGroup());
			}
		} catch (Exception e) {
			logger.error("create META-INF/app.properties error,cause:{}",e.getMessage());
		}
	}
	
	
	// 获取文件内容
	public String getConfig(String fileName, String group) {
		String fileFormart = null;
		if (fileName != null && fileName.lastIndexOf(".") > 0) {
			fileFormart = fileName.substring(fileName.lastIndexOf(".") + 1);
		}
		ConfigFile config = ConfigService.getConfigFile(fileName, ConfigFileFormat.fromString(fileFormart));
		if (config == null) {
			logger.error("Apollo ConfigFile load error,ConfigFile is null");
		}
		return config.getContent();
	}
	
	// 监听需要关注的内容
	public void addListener(String fileName, String group, ConfListener listener) {
		String fileFormart = null;
		if (fileName != null && fileName.lastIndexOf(".") > 0) {
			fileFormart = fileName.substring(fileName.lastIndexOf(".") + 1);
		}
		ConfigFile config = ConfigService.getConfigFile(fileName, ConfigFileFormat.fromString(fileFormart));
		if (config == null) {
			logger.error("Apollo ConfigFile load error,ConfigFile is null");
		}
		config.addChangeListener(new ConfigFileChangeListener() {
			@Override
			public void onChange(ConfigFileChangeEvent changeEvent) {
				listener.receive(changeEvent.getNewValue());
			}
		});
	}


		
}