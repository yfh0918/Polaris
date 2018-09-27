package com.polaris.naming;

import java.util.ArrayList;
import java.util.Arrays;
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
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, ConfClient.get(Constant.NAMING_REGISTRY_ADDRESS_NAME));
        properties.setProperty(PropertyKeyConst.NAMESPACE, ConfClient.get(Constant.NAMING_REGISTRY_ADDRESS_NAME, false));
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
	        String cluster = ConfClient.get(Constant.PROJECT_CONSUMER_CLUSTER, Constant.DEFAULT_VALUE, false);
	        List<String> clusters = Arrays.asList(cluster.split(","));
			Instance instance = naming.selectOneHealthyInstance(key,clusters);
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
	        String cluster = ConfClient.get(Constant.PROJECT_CONSUMER_CLUSTER, Constant.DEFAULT_VALUE, false);
	        List<String> clusters = Arrays.asList(cluster.split(","));
			List<Instance> instances = naming.selectInstances(key, clusters, true);
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
	        String cluster = ConfClient.get(Constant.PROJECR_CLUSTER_NAME, Constant.DEFAULT_VALUE, false);
	        instance.setCluster(new Cluster(cluster));
			naming.registerInstance(ConfClient.get(Constant.PROJECT_NAME, false), instance);
			
		} catch (NacosException e) {
			logger.error(e);
		}
	}

	@Override
	public void deregister(String ip, int port) {
		 try {
	        String cluster = ConfClient.get(Constant.PROJECR_CLUSTER_NAME, Constant.DEFAULT_VALUE, false);
			naming.deregisterInstance(ConfClient.get(Constant.PROJECT_NAME, false), ip, port,cluster);
		} catch (NacosException e) {
			logger.error(e);
		}
	}


}
