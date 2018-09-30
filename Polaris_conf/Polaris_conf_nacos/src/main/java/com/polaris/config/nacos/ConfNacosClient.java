package com.polaris.config.nacos;

import java.util.Properties;
import java.util.concurrent.Executor;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.StringUtil;


@Component
public class ConfNacosClient implements ApplicationListener<ContextRefreshedEvent> { 
	
	private static final LogUtil logger = LogUtil.getInstance(ConfNacosClient.class, false);
	private static ConfNacosClient INSTANCE;
	private ConfigService configService;

	public static ConfNacosClient getInstance(String namespace){
		if (INSTANCE == null) {
			synchronized(ConfNacosClient.class) {
				if (INSTANCE == null) {
					INSTANCE = new ConfNacosClient(namespace);
				}
			}
		}
		return INSTANCE;
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
			
			//addListener
			String[] files = ConfClient.getExtensionProperties();
			if (files != null) {
				for (String dataId : files) {
					addListener(dataId);
				}
			}
			
		} catch (NacosException e) {
			logger.error(e);
		}
		
	}
	
	public String getConfig(String key) {
		return null;
	}
	
	private void addListener(String dataId) throws NacosException {
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
		configService.addListener(dataId, group.toString(), new Listener() {
			@Override
			public void receiveConfigInfo(String configInfo) {
				System.out.println("recieve:" + configInfo);
			}

			@Override
			public Executor getExecutor() {
				return null;
			}
		});
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ConfNacosClient.getInstance(ConfClient.getNameSpace());
	}
	
}