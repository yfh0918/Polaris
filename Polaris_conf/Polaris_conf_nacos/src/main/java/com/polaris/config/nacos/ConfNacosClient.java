package com.polaris.config.nacos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.polaris.comm.Constant;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.config.ConfListener;
import com.polaris.comm.config.ConfigHandlerProvider;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.StringUtil;

public class ConfNacosClient { 
	
	private static final LogUtil logger = LogUtil.getInstance(ConfNacosClient.class, false);
	private volatile static ConfNacosClient INSTANCE;
	private volatile ConfigService configService;
	private volatile List<String> allPropertyFiles;

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
    		return;
    	}
    	iniConfNacos();
	}
	
	private void iniConfNacos() {
		Properties properties = new Properties();
		properties.put(PropertyKeyConst.SERVER_ADDR, ConfClient.getConfigRegistryAddress());
		if (StringUtil.isNotEmpty(ConfClient.getNameSpace())) {
			properties.put(PropertyKeyConst.NAMESPACE, ConfClient.getNameSpace());
		}
		allPropertyFiles = new ArrayList<>();
		try {
			configService = NacosFactory.createConfigService(properties);
	
			//application.properties
			addListener(Constant.DEFAULT_CONFIG_NAME, new ConfListener() {
				@Override
				public void receive(String propertyContent) {
					// TODO Auto-generated method stub
					if (StringUtil.isNotEmpty(propertyContent)) {
						String[] contents = propertyContent.split(Constant.LINE_SEP);
						Map<String, String> cache = new HashMap<>();
						for (String content : contents) {
							String[] keyvalue = ConfigHandlerProvider.getKeyValue(content);
							if (keyvalue != null) {
								cache.put(keyvalue[0], keyvalue[1]);
							}
						}
						ConfigHandlerProvider.updateCache(Constant.DEFAULT_CONFIG_NAME, cache);
						if (!allPropertyFiles.contains(Constant.DEFAULT_CONFIG_NAME)) {
							allPropertyFiles.add(Constant.DEFAULT_CONFIG_NAME);
						}
						
						//先获取自己
						String extendCacheNames = cache.get(Constant.PROJECT_EXTENSION_PROPERTIES);
						String[] extendFiles;
						if (StringUtil.isEmpty(extendCacheNames)) {
							extendFiles = ConfigHandlerProvider.getExtensionLocalProperties();
							loadExtendFiles(extendFiles);
						} 
					}
				}
			});
			
			//扩展文件
			String[] extendFiles = ConfigHandlerProvider.getExtensionLocalProperties();
			if (extendFiles != null) {
				loadExtendFiles(extendFiles);
			}

		
		} catch (NacosException e) {
			logger.error(e);
			throw new IllegalArgumentException(Constant.CONFIG_REGISTRY_ADDRESS_NAME + ":"+ConfClient.getConfigRegistryAddress()+" is not correct ");
		}
	}
	
	private void loadExtendFiles (String[] extendFiles) {
		for (String file : extendFiles) {
			addListener(file, new ConfListener() {
				@Override
				public void receive(String propertyContent) {
					if (StringUtil.isNotEmpty(propertyContent)) {
						String[] contents = propertyContent.split(Constant.LINE_SEP);
						Map<String, String> cache = new HashMap<>();
						for (String content : contents) {
							String[] keyvalue = ConfigHandlerProvider.getKeyValue(content);
							if (keyvalue != null) {
								cache.put(keyvalue[0], keyvalue[1]);
							}
						}
						ConfigHandlerProvider.updateCache(file, cache);
						if (!allPropertyFiles.contains(file)) {
							allPropertyFiles.add(file);
						}
					}
				}
			});
	    }
	}
	
	//获取所有的配置文件
	public List<String> getAllPropertyFiles() {
		return allPropertyFiles;
	}
	
	// 获取key,value
	public String getConfig(String key, String fileName) {
		//配置文件
    	if (StringUtil.isEmpty(ConfClient.getConfigRegistryAddress())) {
    		return null;
    	}
    	if (configService == null) {
    		synchronized(this) {
    			if (configService == null) {
    				iniConfNacos();
    			}
    		}
    	}
    	
		String group = getGroup();
		try {
			String propertyContent = configService.getConfig(fileName, group, 5000);
			if (StringUtil.isNotEmpty(propertyContent)) {
				String[] contents = propertyContent.split(Constant.LINE_SEP);
				for (String content : contents) {
					String[] keyvalue = ConfigHandlerProvider.getKeyValue(content);
					if (keyvalue != null && keyvalue[0].equals(key)) {
						return keyvalue[1];
					}
				}
			}

		} catch (NacosException e) {
			logger.error(e);
		}
		return null;
	}
	
	// 监听需要关注的内容
	public void addListener(String dataId, ConfListener listener) {
		//配置文件
    	if (StringUtil.isEmpty(ConfClient.getConfigRegistryAddress())) {
    		return;
    	}
    	if (configService == null) {
    		synchronized(this) {
    			if (configService == null) {
    				iniConfNacos();
    			}
    		}
    	}
    	
		String group = getGroup();
		try {
			configService.addListener(dataId, group.toString(), new Listener() {
				@Override
				public void receiveConfigInfo(String configInfo) {
					if (listener != null) {
						listener.receive(configInfo);
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

	// 获取分组信息
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