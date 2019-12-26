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
import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.ServerDiscoveryHandler;
import com.polaris.core.util.StringUtil;

@Order(0)
public class NacosServerDiscovery implements ServerDiscoveryHandler {
	private static final Logger logger = LoggerFactory.getLogger(NacosServerDiscovery.class);
	private volatile NamingService naming;
	public NacosServerDiscovery() {
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
        try {
			naming = NamingFactory.createNamingService(properties);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new IllegalArgumentException(Constant.NAMING_REGISTRY_ADDRESS_NAME + ":"+ConfClient.getNamingRegistryAddress() + " is not correct ");
		}
	}
	
	@Override
	public String getUrl(String key) {
		if (StringUtil.isEmpty(key)) {
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
	        String groupName = com.alibaba.nacos.api.common.Constants.DEFAULT_GROUP;
	        if (StringUtil.isNotEmpty(ConfClient.getGroup())) {
	        	groupName = ConfClient.getGroup();
			}

			instance = naming.selectOneHealthyInstance(key, groupName);
			if (instance != null) {
				return instance.toInetAddr();
			}
		} catch (Exception e) {
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
	        String groupName = com.alibaba.nacos.api.common.Constants.DEFAULT_GROUP;
	        if (StringUtil.isNotEmpty(ConfClient.getGroup())) {
	        	groupName = ConfClient.getGroup();
			}

			instances = naming.selectInstances(key, groupName, true, subscribe);

			List<String> urls = new ArrayList<>();
			if (instances != null && instances.size() > 0) {
				for (Instance instance : instances) {
					if (instance.isHealthy()) {
						urls.add(instance.toInetAddr());
					}
				}
			}
			return urls;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public void connectionFail(String key, String url) {
		//nothing
	}

	@Override
	public void register(String ip, int port) {
		if (naming == null) {
			return;
		}
		 try {
			Instance instance = new Instance();
	        instance.setIp(ip);
	        instance.setPort(port);
	        double weight = Double.parseDouble(ConfClient.get(Constant.PROJECT_WEIGHT, Constant.PROJECT_WEIGHT_DEFAULT));
	        instance.setWeight(weight);
	        boolean ephemeral = Boolean.parseBoolean(ConfClient.get(Constant.PROJECT_EPHEMERAL, Constant.PROJECT_EPHEMERAL_DEFAULT));
	        instance.setEphemeral(ephemeral);
	        String groupName = com.alibaba.nacos.api.common.Constants.DEFAULT_GROUP;
	        if (StringUtil.isNotEmpty(ConfClient.getGroup())) {
	        	groupName = ConfClient.getGroup();
			}
        	naming.registerInstance(ConfClient.getAppName(), groupName, instance);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void deregister(String ip, int port) {
		if (naming == null) {
			return;
		}
		 try {
	        //String cluster = ConfClient.getCluster();
	        String groupName = com.alibaba.nacos.api.common.Constants.DEFAULT_GROUP;
	        if (StringUtil.isNotEmpty(ConfClient.getGroup())) {
	        	groupName = ConfClient.getGroup();
			}

			naming.deregisterInstance(ConfClient.getAppName(), groupName, ip, port);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public static void main( String[] args ) throws Exception
    {
//		Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.SERVER_ADDR, "127.0.0.1:8848");
//		NamingService naming = NamingFactory.createNamingService(properties);
//		for (int i0 = 0; i0 < 1000; i0++) {
//			Instance instance = naming.selectOneHealthyInstance("mwclg-sso");
//			System.out.println(instance.toInetAddr());
//		}
//		NacosServerDiscovery ddd = new NacosServerDiscovery();
//		String key1="polaris-demo";
//		String key2="polaris-demo&group=xxx";
//		String key3="polaris-demo&clusters=yyyy1";
//		String key4="polaris-demo&group=xxx&clusters=yyyy1,yyyy2";
//		String[] keyinfo1 = ddd.getKeyInfo(key1);
//		System.out.println(keyinfo1[0]);
//		System.out.println(keyinfo1[1]);
//		System.out.println(ddd.getClusters(keyinfo1[2]));
//		String[] keyinfo2 = ddd.getKeyInfo(key2);
//		System.out.println(keyinfo2[0]);
//		System.out.println(keyinfo2[1]);
//		System.out.println(ddd.getClusters(keyinfo2[2]));
//		String[] keyinfo3 = ddd.getKeyInfo(key3);
//		System.out.println(keyinfo3[0]);
//		System.out.println(keyinfo3[1]);
//		System.out.println(ddd.getClusters(keyinfo3[2]));
//		String[] keyinfo4 = ddd.getKeyInfo(key4);
//		System.out.println(keyinfo4[0]);
//		System.out.println(keyinfo4[1]);
//		System.out.println(ddd.getClusters(keyinfo4[2]));
    }
	
//	//key: polaris-demo&group=xxx&clusters=yyyy1,yyyy2
//	//key: polaris-demo&group=xxx
//	//key: polaris-demo&clusters=yyyy1
//	//key: polaris-demo
//	private String[] getKeyInfo(String key) {
//		String[] keyInfo = key.split("&");
//		if (keyInfo.length == 1) {
//			return new String[] {keyInfo[0],com.alibaba.nacos.api.common.Constants.DEFAULT_GROUP,""};
//		} else if (keyInfo.length == 2) {
//			if (StringUtil.isEmpty(keyInfo[1])) {
//				String group = com.alibaba.nacos.api.common.Constants.DEFAULT_GROUP;
//				String clusters = "";
//				return new String[] {keyInfo[0],group,clusters};
//			}
//			if (keyInfo[1].indexOf("group") > -1) {
//				return new String[] {keyInfo[0],ConfHandlerSupport.getKeyValue(keyInfo[1])[1],""};
//			}
//			if (keyInfo[1].indexOf("clusters") > -1) {
//				String group = com.alibaba.nacos.api.common.Constants.DEFAULT_GROUP;
//				return new String[] {keyInfo[0],group, ConfHandlerSupport.getKeyValue(keyInfo[1])[1]};
//			}
//			return new String[] {keyInfo[0],"",""};
//		} else {
//			String group = com.alibaba.nacos.api.common.Constants.DEFAULT_GROUP;
//			if (StringUtil.isNotEmpty(keyInfo[1]) && keyInfo[1].indexOf("group") > -1) {
//				group = ConfHandlerSupport.getKeyValue(keyInfo[1])[1];
//			}
//			String clusters = "";
//			if (StringUtil.isNotEmpty(keyInfo[2]) && keyInfo[2].indexOf("clusters") > -1) {
//				clusters = ConfHandlerSupport.getKeyValue(keyInfo[2])[1];
//			}
//			return new String[] {keyInfo[0],group,clusters};
//		}
//	}
	

//	private List<String> getClusters(String value) {
//		if (StringUtil.isEmpty(value)) {
//			return new ArrayList<>();
//		}
//		return Arrays.asList(value.split(","));
//	}
	
}
