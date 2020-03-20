package com.polaris.naming;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.client.config.ClientConfigFactory;
import com.netflix.client.config.IClientConfig;
import com.netflix.client.config.IClientConfigKey.Keys;
import com.netflix.config.ConfigurationManager;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.ServerList;
import com.netflix.loadbalancer.ServerListFilter;
import com.netflix.loadbalancer.ZoneAffinityServerListFilter;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.ServerHandlerOrder;
import com.polaris.core.naming.ServerHandler;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.StringUtil;

@Order(ServerHandlerOrder.EUREKA)
public class EurekaServer implements ServerHandler {
	private static final Logger logger = LoggerFactory.getLogger(EurekaServer.class);
	private ApplicationInfoManager applicationInfoManager;
	private DiscoveryClient eurekaClient;
	public EurekaServer() {
	}
	
	private void iniEurekaServer(String ip, int port,String isRegistry) {
		Properties properties = new Properties();
		
		//全局配置
		properties.setProperty("eureka.region", ConfClient.get("eureka.region", "default"));
		properties.setProperty("eureka.registration.enabled", isRegistry);
		properties.setProperty("eureka.preferIpAddress", ConfClient.get("eureka.preferIpAddress", "true"));
		properties.setProperty("eureka.preferSameZone", ConfClient.get("eureka.preferSameZone", "true"));
		properties.setProperty("eureka.shouldUseDns", ConfClient.get("eureka.shouldUseDns", "false"));
		String prefixNamingUrl = null;
		if (ConfClient.getNamingRegistryAddress().startsWith("http://")) {
			prefixNamingUrl = ConfClient.get("eureka.serviceUrl.default", ConfClient.getNamingRegistryAddress());
		} else {
			prefixNamingUrl = "http://"+ConfClient.getNamingRegistryAddress();
		}
		properties.setProperty("eureka.serviceUrl.default", prefixNamingUrl +"/eureka/");
		properties.setProperty("eureka.decoderName", ConfClient.get("eureka.decoderName", "JacksonJson"));
		
		String prefixLocalUrl = "http://"+ip+port+ConfClient.get("server.contextPath", "");		
		if (StringUtil.isNotEmpty(ConfClient.get("eureka.homePageUrl"))) {
			properties.setProperty("eureka.homePageUrl",prefixLocalUrl + ConfClient.get("eureka.homePageUrl"));
		}
		if (StringUtil.isNotEmpty(ConfClient.get("eureka.healthCheckUrl"))) {
			properties.setProperty("eureka.healthCheckUrl",prefixLocalUrl + ConfClient.get("eureka.healthCheckUrl"));
		}
		if (StringUtil.isNotEmpty(ConfClient.get("eureka.statusPageUrl"))) {
			properties.setProperty("eureka.statusPageUrl",prefixLocalUrl + ConfClient.get("eureka.statusPageUrl"));
		}
					
        //应用配置#应用配置
		String group = "default";
        if (StringUtil.isNotEmpty(ConfClient.getGroup())) {
        	group = ConfClient.getGroup();
		}
		properties.setProperty("eureka.appGroup", group);
        properties.setProperty("eureka.name", ConfClient.getAppName());
        properties.setProperty("eureka.vipAddress", ConfClient.getAppName());
        properties.setProperty("eureka.port", String.valueOf(port));
        properties.setProperty("eureka.ipAddr", ip);
        String instanceId = properties.getProperty("eureka.ipAddr") + ":" + properties.getProperty("eureka.port");
        properties.setProperty("eureka.instanceId", instanceId);
		
        //载入配置
        ConfigurationManager.loadProperties(properties);
        MyDataCenterInstanceConfig instanceConfig = null;
        if (StringUtil.isNotEmpty(ConfClient.getNameSpace())) {
            instanceConfig = new MyDataCenterInstanceConfig(ConfClient.getNameSpace());
        } else {
            instanceConfig = new MyDataCenterInstanceConfig();
        }
        InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
        applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        DefaultEurekaClientConfig clientConfig = new DefaultEurekaClientConfig();
        eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);
        logger.info("Eureka ApplicationInfoManager is initialized");
	}
	
	@Override
	public String getUrl(String key) {
		if (StringUtil.isEmpty(key)) {
			return null;
		}
		if (eurekaClient == null) {
			synchronized(this) {
				if (eurekaClient == null) {
					String registerIp = ConfClient.get(Constant.IP_ADDRESS, NetUtils.getLocalHost());
					String port = ConfClient.get(Constant.SERVER_PORT_NAME,ConfClient.get(Constant.DUBBO_PROTOCOL_PORT_NAME,""));
					if (StringUtil.isEmpty(port)) {
						return null;
					}
					iniEurekaServer(registerIp,Integer.parseInt(port), "false");
				}
			}
		}
		if (eurekaClient == null) {
			return null;
		}

		
		//负载均衡
		InstanceInfo serverInfo = getServerInfoFromRobbin(ConfClient.getAppName());
		if (serverInfo == null) {
			serverInfo = eurekaClient.getNextServerFromEureka(key, false);
		}
		if (serverInfo != null) {
		    String realServerName = serverInfo.getIPAddr() + ":" + serverInfo.getPort();
		    return realServerName;
		}
		return null;
	}
	
	@Override
	public List<String> getAllUrls(String key) {
		return getAllUrls(key, true);
	}

	@Override
	public List<String> getAllUrls(String key, boolean subscribe) {
		if (StringUtil.isEmpty(key)) {
			return null;
		}
		if (eurekaClient == null) {
			synchronized(this) {
				if (eurekaClient == null) {
					String registerIp = ConfClient.get(Constant.IP_ADDRESS, NetUtils.getLocalHost());
					String port = ConfClient.get(Constant.SERVER_PORT_NAME,ConfClient.get(Constant.DUBBO_PROTOCOL_PORT_NAME,""));
					if (StringUtil.isEmpty(port)) {
						return null;
					}
					iniEurekaServer(registerIp,Integer.parseInt(port), "false");
				}
			}
		}
		if (eurekaClient == null) {
			return null;
		}
		
		List<InstanceInfo> serverInfos = eurekaClient.getInstancesByVipAddress(key,false);
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
	public boolean connectionFail(String key, String url) {
		return true;
	}

	@Override
	public boolean register(String ip, int port) {
    	if (StringUtil.isEmpty(ConfClient.getNamingRegistryAddress())) {
    		return false;
    	}
    	iniEurekaServer(ip,port, "true");
        waitForRegistrationWithEureka(ip,port);
        return true;
	}

	@Override
	public boolean deregister(String ip, int port) {
		if (eurekaClient == null) {
			return false;
		}
		eurekaClient.shutdown();
		return true;
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
                    	continue;
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e1) {
                    	continue;
                    }
                    logger.info("Waiting 5s... verifying service registration with eureka ...");
                }
            }
        }).start();
    }
	
	//内置robbin负载均衡
	private InstanceInfo getServerInfoFromRobbin(String key) {
		
		try {
			String strClazz = ConfClient.get("robbin.loadbalancer",AvailabilityFilteringRule.class.getName());
			IRule rule = null;
			try {
				rule = (IRule)Class.forName(strClazz).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("create robbin rule error, please check robbin.loadbalancer:{} from properties",strClazz);
				return null;
			} 
			
			Provider<EurekaClient> eurekaProvider = new Provider<EurekaClient> (){
			    @Override
			    public EurekaClient get() {
			        return eurekaClient;
			    }

			};
			IClientConfig clientConfig = ClientConfigFactory.DEFAULT.newConfig();
			clientConfig.set(Keys.DeploymentContextBasedVipAddresses, key);
	        ServerList<DiscoveryEnabledServer> list = new DiscoveryEnabledNIWSServerList(clientConfig,eurekaProvider);
	        ServerListFilter<DiscoveryEnabledServer> filter = new ZoneAffinityServerListFilter<DiscoveryEnabledServer>(clientConfig);
	        ZoneAwareLoadBalancer<DiscoveryEnabledServer> lb = LoadBalancerBuilder.<DiscoveryEnabledServer>newBuilder()
	              .withDynamicServerList(list)
	              .withRule(rule)
	              .withServerListFilter(filter)
	              .buildDynamicServerListLoadBalancer();   
	        DiscoveryEnabledServer server = (DiscoveryEnabledServer) lb.chooseServer();   
	        InstanceInfo serverInfo = server.getInstanceInfo();
	        return serverInfo;
		} catch (Exception ex) {
			logger.error("robbin banlancer failed,caused by {}", ex.getMessage());
		}
		return null;
	}

}
