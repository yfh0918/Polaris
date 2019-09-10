package com.polaris.core.config;

import java.io.File;

import com.polaris.core.Constant;
import com.polaris.core.util.PropertyUtils;
import com.polaris.core.util.StringUtil;

/**
*
* 项目名称：Polaris_core
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
	public static void init() {
		init("");
	}
	public static void init(String conigRootPath) {
		try {
			
			//设置配置文件root路径
			Constant.CONFIG = conigRootPath;
			
	    	// 启动字符集
	    	System.setProperty("file.encoding", "UTF-8");
	    	
			// user.home
	        System.setProperty("user.home", PropertyUtils.getAppPath());

	        //载入application.properties
			ConfigHandlerProvider.loadConfig(Constant.DEFAULT_CONFIG_NAME,false);									
						
			//配置中心
			String config = System.getProperty(Constant.CONFIG_REGISTRY_ADDRESS_NAME);
			if (StringUtil.isNotEmpty(config)) {
				ConfigHandlerProvider.updateValue(Constant.CONFIG_REGISTRY_ADDRESS_NAME, config, Constant.DEFAULT_CONFIG_NAME);
			} 
			
			//工程名称
			String project = System.getProperty(Constant.PROJECT_NAME);
			if (StringUtil.isNotEmpty(project)) {
				ConfigHandlerProvider.updateValue(Constant.PROJECT_NAME, project, Constant.DEFAULT_CONFIG_NAME);
			}
			
			//命名空间(注册中心和配置中心)
			String namespace = System.getProperty(Constant.PROJECR_NAMESPACE_NAME);
			if (StringUtil.isNotEmpty(namespace)) {
				ConfigHandlerProvider.updateValue(Constant.PROJECR_NAMESPACE_NAME, namespace, Constant.DEFAULT_CONFIG_NAME);
			} 
			
			//集群名称(注册中心和配置中心)
			String group = System.getProperty(Constant.PROJECR_GROUP_NAME);
			if (StringUtil.isNotEmpty(group)) {
				ConfigHandlerProvider.updateValue(Constant.PROJECR_GROUP_NAME, group, Constant.DEFAULT_CONFIG_NAME);
			} 
			
			//IP地址
			String serverIp = System.getProperty(Constant.IP_ADDRESS);
			if (StringUtil.isNotEmpty(serverIp)) {
				ConfigHandlerProvider.updateValue(Constant.IP_ADDRESS, serverIp, Constant.DEFAULT_CONFIG_NAME);
			}
			
			//服务端口
			String serverPort = System.getProperty(Constant.SERVER_PORT_NAME);
			if (StringUtil.isNotEmpty(serverPort)) {
				ConfigHandlerProvider.updateValue(Constant.SERVER_PORT_NAME, serverPort, Constant.DEFAULT_CONFIG_NAME);
			}
			
			//注册中心
			String name = System.getProperty(Constant.NAMING_REGISTRY_ADDRESS_NAME);
			if (StringUtil.isNotEmpty(name)) {
				ConfigHandlerProvider.updateValue(Constant.NAMING_REGISTRY_ADDRESS_NAME, name, Constant.DEFAULT_CONFIG_NAME);
			}
			
			//dubbo服务端口
			String dubboPort = System.getProperty(Constant.DUBBO_PROTOCOL_PORT_NAME);
			if (StringUtil.isNotEmpty(dubboPort)) {
				ConfigHandlerProvider.updateValue(Constant.DUBBO_PROTOCOL_PORT_NAME, dubboPort, Constant.DEFAULT_CONFIG_NAME);
			}
			//dubbo注册中心
			String dubboName = System.getProperty(Constant.DUBBO_REGISTRY_ADDRESS_NAME);
			if (StringUtil.isNotEmpty(dubboName)) {
				ConfigHandlerProvider.updateValue(Constant.DUBBO_REGISTRY_ADDRESS_NAME, dubboName, Constant.DEFAULT_CONFIG_NAME);
			}
			
			//载入扩展文件
			String[] extendProperties = ConfHandlerSupport.getExtensionProperties();
			if (extendProperties != null) {
				for (String file : extendProperties) {
					ConfigHandlerProvider.loadConfig(file,false);//载入缓存
				}
			}
			
			//载入全局文件
			if (StringUtil.isNotEmpty(ConfClient.getGlobalConfigName())) {
				String[] globalProperties = ConfHandlerSupport.getGlobalProperties();
				if (globalProperties != null) {
					for (String file : globalProperties) {
						ConfigHandlerProvider.loadConfig(file,true);
					}
				}
			}		

		} catch (Exception e) {
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
		if (StringUtil.isNotEmpty(ConfClient.getGlobalConfigName())) {
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
			ConfigHandlerProvider.updateValue(key, defaultVal, Constant.DEFAULT_CONFIG_NAME);
		}
		
		//返回默认值
		return defaultVal;
	}
	
	//在设置应用名称的时候启动各项参数载入
	public static String getAppName() {
		String appName = ConfigHandlerProvider.getValue(Constant.PROJECT_NAME, Constant.DEFAULT_CONFIG_NAME, false);
		if (StringUtil.isEmpty(appName)) {
			appName = ConfigHandlerProvider.getValue(Constant.SPRING_BOOT_NAME, Constant.DEFAULT_CONFIG_NAME, false);
		}
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
	
	
	//获取分组名称
	public static String getConfig(boolean isGlobal) {
		if (isGlobal) {
			return ConfClient.getGlobalConfigName();
		}
		return ConfClient.getAppName();
	}
	private static String getGlobalConfigName() {
		String globalGroupName = ConfigHandlerProvider.getValue(Constant.PROJECR_GLOBAL_CONFIG_NAME, Constant.DEFAULT_CONFIG_NAME, false);
		return globalGroupName == null ? "" :globalGroupName;
	}

	
	public static String getConfigFileName(String fileName) {
		if (StringUtil.isNotEmpty(Constant.CONFIG)) {
			return Constant.CONFIG + File.separator + fileName;
		}
		return fileName;
	}
}
