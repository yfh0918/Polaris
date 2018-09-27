package com.polaris.naming;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Cluster;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.LogUtil;
import com.polaris.core.connect.ServerDiscoveryHandler;


public class NacosServerDiscovery implements ServerDiscoveryHandler {
	private static final LogUtil logger = LogUtil.getInstance(NacosServerDiscovery.class, false);
	NamingService naming;
	public NacosServerDiscovery() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, ConfClient.get(Constant.NAMING_REGISTRY_ADDRESS_NAME, false));
        properties.setProperty(PropertyKeyConst.NAMESPACE, ConfClient.getNameSpace());
        try {
			naming = NamingFactory.createNamingService(properties);
		} catch (NacosException e) {
			logger.error(e);
		}

	}
	
	@Override
	public String getUrl(String key) {
		// TODO Auto-generated method stub
		try {
//	        String cluster = ConfClient.getCluster();
//	        List<String> clusters = Arrays.asList(cluster.split(","));
			Instance instance = naming.selectOneHealthyInstance(key);
			return instance.toInetAddr();
		} catch (NacosException e) {
			logger.error(e);
		}
		return null;
	}

	@Override
	public List<String> getAllUrls(String key) {
		// TODO Auto-generated method stub
		try {
//	        String cluster = ConfClient.getCluster();
//	        List<String> clusters = Arrays.asList(cluster.split(","));
			List<Instance> instances = naming.selectInstances(key,  true);
			List<String> urls = new ArrayList<>();
			for (Instance instance : instances) {
				urls.add(instance.toInetAddr());
			}
			return urls;
		} catch (NacosException e) {
			logger.error(e);
		}
		return null;
	}

	@Override
	public void connectionFail(String key, String url) {
		//nothing
	}

	@Override
	public void register(String ip, int port) {
		 try {
			Instance instance = new Instance();
	        instance.setIp(ip);
	        instance.setPort(port);
	        double weight = Double.parseDouble(ConfClient.get(Constant.PROJECT_WEIGHT, Constant.PROJECT_WEIGHT_DEFAULT, false));
	        instance.setWeight(weight);
	        String cluster = ConfClient.getCluster();
	        instance.setCluster(new Cluster(cluster));
			naming.registerInstance(ConfClient.getAppName(), instance);
			
		} catch (NacosException e) {
			logger.error(e);
		}
	}

	@Override
	public void deregister(String ip, int port) {
		 try {
	        String cluster = ConfClient.getCluster();
			naming.deregisterInstance(ConfClient.getAppName(), ip, port,cluster);
		} catch (NacosException e) {
			logger.error(e);
		}
	}
	
	public static void main( String[] args ) throws Exception
    {
		Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, "127.0.0.1:8848");
        properties.setProperty(PropertyKeyConst.NAMESPACE, "default");
		NamingService naming = NamingFactory.createNamingService(properties);
		for (int i0 = 0; i0 < 1000; i0++) {
			Instance instance = naming.selectOneHealthyInstance("mwclg-sso");
			System.out.println(instance.toInetAddr());
		}
		
    }


}
