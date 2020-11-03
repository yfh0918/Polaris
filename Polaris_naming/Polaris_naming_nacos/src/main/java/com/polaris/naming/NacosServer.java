package com.polaris.naming;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.polaris.naming.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.NamingHandler;
import com.polaris.core.naming.NamingHandlerOrder;
import com.polaris.core.pojo.Server;
import com.polaris.core.util.StringUtil;

@Order(NamingHandlerOrder.NACOS)
public class NacosServer implements NamingHandler {
	private static final Logger logger = LoggerFactory.getLogger(NacosServer.class);
	private volatile NamingService naming;
	public NacosServer() {
    	if (StringUtil.isEmpty(ConfClient.getNamingRegistryAddress())) {
    		return;
    	}
    	iniNacosServer();
	}
	
	private void iniNacosServer() {
		Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, ConfClient.getNamingRegistryAddress());
        if (StringUtil.isNotEmpty(ConfClient.getNameSpace())) {
            properties.setProperty(PropertyKeyConst.NAMESPACE, ConfClient.getNameSpace());
        }
        if (StringUtil.isNotEmpty(ConfClient.get(Constant.NAMING_REGISTRY_USERNAME))) {
            properties.put(PropertyKeyConst.USERNAME, ConfClient.get(Constant.NAMING_REGISTRY_USERNAME));
        }
        if (StringUtil.isNotEmpty(ConfClient.get(Constant.NAMING_REGISTRY_PASSWORD))) {
            properties.put(PropertyKeyConst.PASSWORD, ConfClient.get(Constant.NAMING_REGISTRY_PASSWORD));
        }
        try {
			naming = NamingFactory.createNamingService(properties);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new IllegalArgumentException(Constant.NAMING_REGISTRY_ADDRESS_NAME + ":"+ConfClient.getNamingRegistryAddress() + " is not correct ");
		}
	}
	
	@Override
	public Server getServer(String serviceName) {
		if (StringUtil.isEmpty(serviceName)) {
			return null;
		}
		//判断是否可以获取有效URL
		if (StringUtil.isEmpty(ConfClient.getNamingRegistryAddress())) {
			return null;
		}
		if (naming == null) {
			synchronized(this) {
				if (naming == null) {
					iniNacosServer();
				}
			}
		} 
		
		//获取有效URL
		try {
			Instance instance = null;
			instance = naming.selectOneHealthyInstance(serviceName, ConfClient.getAppGroup());
			if (instance != null) {
				return Server.of(instance.getIp(), instance.getPort(), new Double(instance.getWeight()).intValue());
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	@Override
	public List<Server> getServerList(String serviceName) {
		
		if (StringUtil.isEmpty(serviceName)) {
			return null;
		}

		//判断是否可以获取有效URL
		if (StringUtil.isEmpty(ConfClient.getNamingRegistryAddress())) {
			return null;
		}
		if (naming == null) {
			synchronized(this) {
				if (naming == null) {
					iniNacosServer();
				}
			}
		} 
				
		//获取有效URL
		try {
			List<Instance> instances = null;
	        String groupName = ConfClient.getAppGroup();
			instances = naming.selectInstances(serviceName, groupName, true, false);

			List<Server> serverList = new ArrayList<>();
			if (instances != null && instances.size() > 0) {
				for (Instance instance : instances) {
					if (instance.isHealthy()) {
						serverList.add(Server.of(instance.getIp(), instance.getPort(), new Double(instance.getWeight()).intValue()));
					}
				}
			}
			return serverList;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public boolean register(Server server) {
		if (naming == null) {
			return false;
		}
		String ip = server.getIp();
		int port = server.getPort();
		 try {
			Instance instance = new Instance();
	        instance.setIp(ip);
	        instance.setPort(port);
	        double weight = Double.parseDouble(ConfClient.get(Constant.PROJECT_WEIGHT, Constant.PROJECT_WEIGHT_DEFAULT));
	        instance.setWeight(weight);
	        boolean ephemeral = Boolean.parseBoolean(ConfClient.get(Constant.PROJECT_EPHEMERAL, Constant.PROJECT_EPHEMERAL_DEFAULT));
	        instance.setEphemeral(ephemeral);
        	naming.registerInstance(ConfClient.getAppName(), ConfClient.getAppGroup(), instance);
        	return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	@Override
	public boolean deregister(Server server) {
		if (naming == null) {
			return false;
		}
		 try {
	        String groupName = ConfClient.getAppGroup();
			naming.deregisterInstance(ConfClient.getAppName(), groupName, server.getIp(), server.getPort());
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}
	
}
