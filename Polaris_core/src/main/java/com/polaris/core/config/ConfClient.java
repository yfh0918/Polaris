package com.polaris.core.config;

import java.io.File;
import java.io.IOException;

import com.polaris.core.Constant;
import com.polaris.core.util.PropertyUtils;
import com.polaris.core.util.StringUtil;

/**
*
* 项目名称：Polaris_comm
* 类名称：ConfClient
* 类描述：
* 创建人：yufenghua
* 创建时间：2018年5月9日 上午9:15:12
* 修改人：yufenghua
* 修改时间：2018年5月9日 上午9:15:12
* 修改备注：
* @version
*
*/
public class ConfClient {
	
	
	//初始化操作
	public static void init(String appName) {
		try {
			//载入日志文件
			System.setProperty("log4j.configurationFile", PropertyUtils.getFilePath(Constant.CONFIG + File.separator + Constant.LOG4J));

			//载入application.properties
			ConfigHandlerProvider.loadConfig(Constant.DEFAULT_CONFIG_NAME,false);
			
			//APPName
			if (StringUtil.isEmpty(appName)) {
				appName = System.getProperty(Constant.PROJECT_NAME);
			}
			if (StringUtil.isNotEmpty(appName)) {
				ConfigHandlerProvider.updateCache(Constant.PROJECT_NAME, appName, Constant.DEFAULT_CONFIG_NAME);
			}
						
	    	// 启动字符集
	    	System.setProperty("file.encoding", "UTF-8");
	    	
			// user.home
	        System.setProperty("user.home", PropertyUtils.getAppPath());
						
			//配置中心
			String config = System.getProperty(Constant.CONFIG_REGISTRY_ADDRESS_NAME);
			if (StringUtil.isNotEmpty(config)) {
				ConfigHandlerProvider.updateCache(Constant.CONFIG_REGISTRY_ADDRESS_NAME, config, Constant.DEFAULT_CONFIG_NAME);
			} 
			
			//环境（production, pre,dev,pre etc）
			String env = System.getProperty(Constant.PROJECT_ENV_NAME);
			if (StringUtil.isNotEmpty(env)) {
				ConfigHandlerProvider.updateCache(Constant.PROJECT_ENV_NAME, env, Constant.DEFAULT_CONFIG_NAME);
			} 
			
			//工程名称
			String project = System.getProperty(Constant.PROJECT_NAME);
			if (StringUtil.isNotEmpty(project)) {
				ConfigHandlerProvider.updateCache(Constant.PROJECT_NAME, project, Constant.DEFAULT_CONFIG_NAME);
			}
			
			//命名空间(注册中心和配置中心)
			String namespace = System.getProperty(Constant.PROJECR_NAMESPACE_NAME);
			if (StringUtil.isNotEmpty(namespace)) {
				ConfigHandlerProvider.updateCache(Constant.PROJECR_NAMESPACE_NAME, namespace, Constant.DEFAULT_CONFIG_NAME);
			} 
			
			//Group(注册中心和dubbo)
			String group = System.getProperty(Constant.PROJECR_GROUP_NAME);
			if (StringUtil.isNotEmpty(group)) {
				ConfigHandlerProvider.updateCache(Constant.PROJECR_GROUP_NAME, group, Constant.DEFAULT_CONFIG_NAME);
			} 

			//集群名称(注册中心和配置中心)
			String cluster = System.getProperty(Constant.PROJECR_CLUSTER_NAME);
			if (StringUtil.isNotEmpty(cluster)) {
				ConfigHandlerProvider.updateCache(Constant.PROJECR_CLUSTER_NAME, cluster, Constant.DEFAULT_CONFIG_NAME);
			} 
			
			//服务端口
			String serverport = System.getProperty(Constant.SERVER_PORT_NAME);
			if (StringUtil.isNotEmpty(serverport)) {
				ConfigHandlerProvider.updateCache(Constant.SERVER_PORT_NAME, serverport, Constant.DEFAULT_CONFIG_NAME);
			}
			
			//注册中心
			String name = System.getProperty(Constant.NAMING_REGISTRY_ADDRESS_NAME);
			if (StringUtil.isNotEmpty(name)) {
				ConfigHandlerProvider.updateCache(Constant.NAMING_REGISTRY_ADDRESS_NAME, name, Constant.DEFAULT_CONFIG_NAME);
			}
			
			//dubbo服务端口
			String dubboport = System.getProperty(Constant.DUBBO_PROTOCOL_PORT_NAME);
			if (StringUtil.isNotEmpty(dubboport)) {
				ConfigHandlerProvider.updateCache(Constant.DUBBO_PROTOCOL_PORT_NAME, dubboport, Constant.DEFAULT_CONFIG_NAME);
			}
			//dubbo注册中心
			String dubboname = System.getProperty(Constant.DUBBO_REGISTRY_ADDRESS_NAME);
			if (StringUtil.isNotEmpty(dubboname)) {
				ConfigHandlerProvider.updateCache(Constant.DUBBO_REGISTRY_ADDRESS_NAME, dubboname, Constant.DEFAULT_CONFIG_NAME);
			}
			
			//载入扩展文件
			String[] extendProperties = ConfHandlerSupport.getExtensionProperties();
			if (extendProperties != null) {
				for (String file : extendProperties) {
					ConfigHandlerProvider.loadConfig(file,false);//载入缓存
				}
			}
			
			//载入全局文件
			if (StringUtil.isNotEmpty(ConfClient.getGlobalGroup())) {
				String[] globalProperties = ConfHandlerSupport.getGlobalProperties();
				if (globalProperties != null) {
					for (String file : globalProperties) {
						ConfigHandlerProvider.loadConfig(file,true);
					}
				}
			}		

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	* 获取配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String get(String key) {
		return get(key, "");
	}
	public static String get(String key, String defaultVal) {
		
		//application.properties
		String value = ConfigHandlerProvider.getValue(key,Constant.DEFAULT_CONFIG_NAME,false);
		if (value != null) {
			return value;
		}
		
		//扩展文件
		String[] allProperties = ConfHandlerSupport.getExtensionProperties();
		if (allProperties != null) {
			for (String file : allProperties) {
				value = ConfigHandlerProvider.getValue(key, file, false);
				if (value != null) {
					return value;
				}
			}
		}
		
		//全局
		if (StringUtil.isNotEmpty(ConfClient.getGlobalGroup())) {
			String[] globalProperties = ConfHandlerSupport.getGlobalProperties();
			if (globalProperties != null) {
				for (String file : globalProperties) {
					value = ConfigHandlerProvider.getValue(key, file, true);
					if (value != null) {
						return value;
					}
				}
			}
		}		
		
		//默认值
		if (StringUtil.isNotEmpty(defaultVal)) {
			ConfigHandlerProvider.updateCache(key, defaultVal, Constant.DEFAULT_CONFIG_NAME);
		}
		
		//返回默认值
		return defaultVal;
	}
	
	//在设置应用名称的时候启动各项参数载入
	public static void setAppName(String inputAppName) {
		init(inputAppName);
	}
	public static String getAppName() {
		String appName = ConfigHandlerProvider.getValue(Constant.PROJECT_NAME, Constant.DEFAULT_CONFIG_NAME, false);
		return appName == null ? "" :appName;
	}

	//获取配置文件
	public static String getConfigValue(String fileName) {
		return getConfigValue(fileName,false);
	}
	public static String getConfigValue(String fileName, boolean isGlobal) {
		return ConfigHandlerProvider.getConfig(fileName, isGlobal);
	}

	//增加文件是否修改的监听
	public static void addListener(String fileName, ConfListener listener) {
		addListener(fileName, false, listener);
	}
	public static void addListener(String fileName, boolean isGlobal, ConfListener listener) {
		ConfigHandlerProvider.addListener(fileName, isGlobal, listener);
	}

	public static String getConfigRegistryAddress() {
		String config = ConfigHandlerProvider.getValue(Constant.CONFIG_REGISTRY_ADDRESS_NAME, Constant.DEFAULT_CONFIG_NAME, false);
		return config == null ? "" :config;
	}
	public static String getNameSpace() {
		String namespace = ConfigHandlerProvider.getValue(Constant.PROJECR_NAMESPACE_NAME, Constant.DEFAULT_CONFIG_NAME, false);
		return namespace == null ? "" :namespace;
	}
	public static String getGroup() {
		String group = ConfigHandlerProvider.getValue(Constant.PROJECR_GROUP_NAME, Constant.DEFAULT_CONFIG_NAME, false);
		return group == null ? "" :group;
	}

	public static String getCluster() {
		String cluster = ConfigHandlerProvider.getValue(Constant.PROJECR_CLUSTER_NAME, Constant.DEFAULT_CONFIG_NAME, false);
		return cluster == null ? "" :cluster;
	}
	public static String getEnv() {
		String env = ConfigHandlerProvider.getValue(Constant.PROJECT_ENV_NAME, Constant.DEFAULT_CONFIG_NAME, false);
		return env == null ? "" :env;
	}
	public static String getNamingRegistryAddress() {
		String naming = ConfigHandlerProvider.getValue(Constant.NAMING_REGISTRY_ADDRESS_NAME, Constant.DEFAULT_CONFIG_NAME, false);
		return naming == null ? "" :naming;
	}
	public static long getUuidWorkId() {
		String workId = ConfigHandlerProvider.getValue(Constant.UUID_WORKID, Constant.DEFAULT_CONFIG_NAME, false);
		if (StringUtil.isEmpty(workId)) {
			return 0l;
		}
		return Long.parseLong(workId.trim());
	}
	public static long getUuidDatacenterId() {
		String datacenterId = ConfigHandlerProvider.getValue(Constant.UUID_DATACENTERID, Constant.DEFAULT_CONFIG_NAME, false);
		if (StringUtil.isEmpty(datacenterId)) {
			return 0l;
		}
		return Long.parseLong(datacenterId.trim());
	}	
	public static String getGlobalGroup() {
		String globalGroupName = ConfigHandlerProvider.getValue(Constant.PROJECR_GLOBAL_GROUP_NAME, Constant.DEFAULT_CONFIG_NAME, false);
		return globalGroupName == null ? "" :globalGroupName;
	}
	
	
}
