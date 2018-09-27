package com.polaris.config.nacos;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.StringUtil;



public class ConfNacosClient  {
	
	private static final LogUtil logger = LogUtil.getInstance(ConfNacosClient.class, false);
	private static final Map<String, ConfNacosClient> clientMap = new ConcurrentHashMap<>();
	private ConfigService configService;

	public static ConfNacosClient getInstance(String namespace){
		if (clientMap.get(namespace) == null) {
			synchronized(namespace.intern()) {
				if (clientMap.get(namespace) == null) {
					clientMap.put(namespace, new ConfNacosClient(namespace));
				}
			}
		}
		return clientMap.get(namespace);
	}

	private ConfNacosClient(String namespace) {
		//配置文件
    	if (StringUtil.isEmpty(ConfClient.getConfigRegistryAddress())) {
    		throw new NullPointerException(Constant.CONFIG_REGISTRY_ADDRESS_NAME + " is null");
    	}
		Properties properties = new Properties();
		properties.put(PropertyKeyConst.SERVER_ADDR, ConfClient.getConfigRegistryAddress());
		properties.put(PropertyKeyConst.NAMESPACE, ConfClient.getNameSpace());
		try {
			configService = NacosFactory.createConfigService(properties);
		} catch (NacosException e) {
			logger.error(e);
		}
	}
	
	public String getConfig(String dataId, String group, boolean isWatch) {
		try {
			String content = configService.getConfig(dataId, group, 5000);
			if (isWatch) {
				configService.addListener(dataId, group, new Listener() {
					@Override
					public void receiveConfigInfo(String configInfo) {
						ConfClient.update(dataId, configInfo);
					}

					@Override
					public Executor getExecutor() {
						return null;
					}
				});
			}
			return content;
		} catch (NacosException ex) {
			return null;
		}
	}
}