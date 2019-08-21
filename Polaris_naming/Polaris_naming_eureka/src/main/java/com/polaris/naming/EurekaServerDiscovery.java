package com.polaris.naming;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.config.ConfigurationManager;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.ServerDiscoveryHandler;
import com.polaris.core.util.StringUtil;


public class EurekaServerDiscovery implements ServerDiscoveryHandler {
	private static final Logger logger = LoggerFactory.getLogger(EurekaServerDiscovery.class);
	private ApplicationInfoManager applicationInfoManager;
	private DiscoveryClient eurekaClient;
	public EurekaServerDiscovery() {
	}
	
	private void iniEurekaServer(String ip, int port) {
		Properties properties = new Properties();
		
		//全局配置
		properties.setProperty("eureka.region", ConfClient.get("eureka.region", "default"));
		properties.setProperty("eureka.registration.enabled", ConfClient.get("eureka.registration.enabled", "true"));
		properties.setProperty("eureka.preferIpAddress", ConfClient.get("eureka.preferIpAddress", "true"));
		properties.setProperty("eureka.preferSameZone", ConfClient.get("eureka.preferSameZone", "true"));
		properties.setProperty("eureka.shouldUseDns", ConfClient.get("eureka.shouldUseDns", "false"));
		properties.setProperty("eureka.serviceUrl.default", ConfClient.get("eureka.serviceUrl.default", ConfClient.getNamingRegistryAddress() +"/eureka/"));
		properties.setProperty("eureka.decoderName", ConfClient.get("eureka.decoderName", "JacksonJson"));
					
        //应用配置#应用配置
        properties.setProperty("server.port", ConfClient.get("server.port"));
        properties.setProperty("eureka.name", ConfClient.getAppName());
        properties.setProperty("eureka.vipAddress", ConfClient.getAppName());
        properties.setProperty("eureka.port", String.valueOf(port));
        properties.setProperty("eureka.ipAddr", ip);
        String instanceId = properties.getProperty("eureka.ipAddr") + ":" + properties.getProperty("eureka.port") + "/" + properties.getProperty("eureka.name");
        properties.setProperty("eureka.instanceId", instanceId);

        //载入配置
        ConfigurationManager.loadProperties(properties);
        MyDataCenterInstanceConfig instanceConfig = new MyDataCenterInstanceConfig();
        InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
        applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        DefaultEurekaClientConfig clientConfig = new DefaultEurekaClientConfig();
        eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);
        logger.info("Eureka ApplicationInfoManager is initialized");
	}
	
	@Override
	public String getUrl(String key) {
		if (eurekaClient == null) {
			return null;
		}
		if (StringUtil.isEmpty(key)) {
			return null;
		}
		
		//负载均衡
		InstanceInfo serverInfo = eurekaClient.getNextServerFromEureka(key, false);
		if (serverInfo != null) {
		    String realServerName = serverInfo.getIPAddr() + ":" + serverInfo.getPort();
		    return realServerName;
		}
		return null;
	}
	

	@Override
	public List<String> getAllUrls(String key) {
		if (eurekaClient == null) {
			return null;
		}
		if (StringUtil.isEmpty(key)) {
			return null;
		}
		List<InstanceInfo> serverInfos = eurekaClient.getInstancesById(key);
		if (serverInfos == null) {
			return null;
		}
		List<String> urlList = new ArrayList<>(serverInfos.size());
		for (InstanceInfo serverInfo : serverInfos) {
			urlList.add(serverInfo.getIPAddr() + ":" + serverInfo.getPort());
		}
		return urlList;
	}

	@Override
	public void connectionFail(String key, String url) {
		//nothing
	}

	@Override
	public void register(String ip, int port) {
    	if (StringUtil.isEmpty(ConfClient.getNamingRegistryAddress())) {
    		return;
    	}
    	iniEurekaServer(ip,port);
        waitForRegistrationWithEureka(ip,port);
	}

	@Override
	public void deregister(String ip, int port) {
		if (eurekaClient == null) {
			return;
		}
		eurekaClient.shutdown();
	}

	//注册结果
	private void waitForRegistrationWithEureka(String ip, int port) {

        //开启一个线程验证注册结果
        new Thread(new Runnable() {
            @Override
            public void run() {
        		if (applicationInfoManager == null) {
        			return;
        		}
                applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.STARTING);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
                long startTime = System.currentTimeMillis();
                while (true) {
                    if (System.currentTimeMillis() - startTime > 5000 * 5) {
                        logger.warn(" >>>> service registration status not verify,please check it!!!!");
                        return;
                    }
                    try {
                        List<InstanceInfo> serverInfos = eurekaClient.getInstancesByVipAddress(ConfClient.getAppName(), false);
                        for (InstanceInfo nextServerInfo : serverInfos) {
                        	if (nextServerInfo.getIPAddr().equals(ip) && nextServerInfo.getPort() == port) {
                        		logger.info("verifying service registration with eureka finished");
                                return;
                        	}
                            
                        }
                    } catch (Throwable e) {
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e1) {
                    }
                    logger.info("Waiting 5s... verifying service registration with eureka ...");
                }
            }
        }).start();
    }

}
