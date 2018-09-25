package com.polaris.config.nacos;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.LogUtil;



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
		Properties properties = new Properties();
		properties.put(PropertyKeyConst.SERVER_ADDR, Constant.CONFIG_REGISTRY_ADDRESS);
		properties.put(PropertyKeyConst.NAMESPACE, ConfClient.getNameSpace());
		try {
			configService = NacosFactory.createConfigService(properties);
		} catch (NacosException e) {
			logger.error(e);
		}
	}
	
	

}