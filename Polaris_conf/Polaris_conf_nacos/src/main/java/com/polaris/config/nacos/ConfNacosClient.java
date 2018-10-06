package com.polaris.config.nacos;

import java.util.Properties;
import java.util.concurrent.Executor;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.config.ConfListener;
import com.polaris.comm.config.ConfigHandlerProvider;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.StringUtil;

public class ConfNacosClient { 
	
	private static final LogUtil logger = LogUtil.getInstance(ConfNacosClient.class, false);
	private static ConfNacosClient INSTANCE;
	private ConfigService configService;

	public static ConfNacosClient getInstance(){
		if (INSTANCE == null) {
			synchronized(ConfNacosClient.class) {
				if (INSTANCE == null) {
					INSTANCE = new ConfNacosClient();
				}
			}
		}
		return INSTANCE;
	}

	private ConfNacosClient() {
		//配置文件
    	if (StringUtil.isEmpty(ConfClient.getConfigRegistryAddress())) {
    		throw new NullPointerException(Constant.CONFIG_REGISTRY_ADDRESS_NAME + " is null");
    	}
		Properties properties = new Properties();
		properties.put(PropertyKeyConst.SERVER_ADDR, ConfClient.getConfigRegistryAddress());
		properties.put(PropertyKeyConst.NAMESPACE, ConfClient.getNameSpace());
		try {
			configService = NacosFactory.createConfigService(properties);
			
			//addListener
			String[] files = ConfigHandlerProvider.getExtensionProperties();
			if (files != null) {
				for (String dataId : files) {
					addListener(dataId, null);
				}
			}
			
		} catch (NacosException e) {
			logger.error(e);
		}
		
	}
	
	public String getConfig(String key) {
		String group = getGroup();
		try {
			String value = configService.getConfig(key, group, 5000);
			return value;
		} catch (NacosException e) {
			logger.error(e);
		}
		return null;
	}
	
	public String getFileContent(String fileName) {
		String group = getGroup();
		String content = null;
		try {
			content = configService.getConfig(fileName, group, 5000L);
		} catch (NacosException e) {
			logger.error(e);
		}
		return content;
	}
	
	public void addListener(String dataId, ConfListener listener) {
		String group = getGroup();
		try {
			configService.addListener(dataId, group.toString(), new Listener() {
				@Override
				public void receiveConfigInfo(String configInfo) {
					if (listener != null) {
						listener.receive(configInfo);
					} else {
						loadLocalCacheFromNacos(configInfo);
					}
				}

				@Override
				public Executor getExecutor() {
					return null;
				}
			});
		} catch (NacosException e) {
			logger.error(e);
		}
	}
	
	//监听到的发生变化的配置更新到本地缓存
	private void loadLocalCacheFromNacos(String propertyContent) {
		if (StringUtil.isNotEmpty(propertyContent)) {
			String[] contents = propertyContent.split(Constant.LINE_SEP);
			for (String content : contents) {
				int index = content.indexOf("=");
				if (index >= 0) {
					String key = content.substring(0, index).trim();
					String value = "";
					if (index < content.length()) {
						value = content.substring(index + 1).trim();
					}
					ConfClient.update(key, value);
				}
				
			}
		}
	}

	private String getGroup() {
		StringBuilder group = new StringBuilder();
		if (StringUtil.isNotEmpty(ConfClient.getEnv())) {
			group.append(ConfClient.getEnv());
			group.append("-");
		}
		if (StringUtil.isNotEmpty(ConfClient.getCluster())) {
			group.append(ConfClient.getCluster());
			group.append("-");
		}
		group.append(ConfClient.getAppName());
		return group.toString();
	}
	
}