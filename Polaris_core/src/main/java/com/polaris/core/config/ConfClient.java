package com.polaris.core.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.util.EnvironmentUtil;
import com.polaris.core.util.NetUtils;
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
	
	final static Logger logger = LoggerFactory.getLogger(ConfClient.class);
	
	static volatile boolean initial = false;
	
	//初始化操作
	public static void init() {
		try {
			
			//已经初期化直接退出
			if (initial) {
				return;
			}
			initial = true;
			
	    	// 启动字符集
	    	System.setProperty(Constant.FILE_ENCODING, Constant.UTF_CODE);
	    	
	    	// 设置文件
	    	String projectConfigLocation = System.getProperty(Constant.PROJECT_CONFIG_NAME);
	    	if (StringUtil.isNotEmpty(projectConfigLocation)) {
	    		Constant.DEFAULT_CONFIG_NAME = projectConfigLocation;
	    	}
	    	
	        //载入application.properties
			ConfigHandlerProvider.loadConfig(Constant.DEFAULT_CONFIG_NAME,false);
			
			//查询所有systemenv
			for (Map.Entry<String, String> entry : EnvironmentUtil.getSystemEnvironment().entrySet()) {
				if (StringUtil.isNotEmpty(entry.getValue())) {
					ConfigHandlerProvider.updateValue(entry.getKey(), entry.getValue(), Constant.DEFAULT_CONFIG_NAME);
				}
			}
			
			//查询所有systemProperties
			for (Map.Entry<String, String> entry : EnvironmentUtil.getSystemProperties().entrySet()) {
				if (StringUtil.isNotEmpty(entry.getValue())) {
					ConfigHandlerProvider.updateValue(entry.getKey(), entry.getValue(), Constant.DEFAULT_CONFIG_NAME);
				}
			}
			
			//IP地址单独处理
			String serverIp = ConfClient.get(Constant.IP_ADDRESS);
			if (StringUtil.isEmpty(serverIp)) {
				ConfigHandlerProvider.updateValue(Constant.IP_ADDRESS, NetUtils.getLocalHost(), Constant.DEFAULT_CONFIG_NAME);
			} 
			
			//载入扩展文件
			String[] extendProperties = ConfHandlerSupport.getExtensionProperties();
			if (extendProperties != null) {
				for (String file : extendProperties) {
					ConfigHandlerProvider.loadConfig(file,false);//载入缓存
				}
			}
			
			//载入全局文件
			if (StringUtil.isNotEmpty(ConfigHandlerProvider.getValue(Constant.PROJECR_GLOBAL_CONFIG_NAME, Constant.DEFAULT_CONFIG_NAME, false))) {
				String[] globalProperties = ConfHandlerSupport.getGlobalProperties();
				if (globalProperties != null) {
					for (String file : globalProperties) {
						ConfigHandlerProvider.loadConfig(file,true);
					}
				}
			}		

		} catch (Exception e) {
			logger.error(e.getMessage());
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
		if (StringUtil.isNotEmpty(ConfigHandlerProvider.getValue(Constant.PROJECR_GLOBAL_CONFIG_NAME, Constant.DEFAULT_CONFIG_NAME, false))) {
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

}
