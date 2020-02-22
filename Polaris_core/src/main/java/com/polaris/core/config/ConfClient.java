package com.polaris.core.config;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.util.EnvironmentUtil;
import com.polaris.core.util.NetUtils;
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
public abstract class ConfClient {
	private static final Logger logger = LoggerFactory.getLogger(ConfClient.class);
	
	//初始化标志
	private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
	
	//初始化操作
	public static void init() {
		
		//初始化
		if (!initialized.compareAndSet(false, true)) {
            return;
        }
		
    	//初始DEFAULT_CONFIG
		initDefault();
		
		//载入扩展文件
		initExtend();
		
		//载入全局文件
		initGlobal();
	}
	
	private static void initDefault() {
		
    	// 启动字符集
    	System.setProperty(Constant.FILE_ENCODING, Constant.UTF_CODE);

		// 设置文件
    	String projectConfigLocation = System.getProperty(Constant.SPRING_CONFIG_LOCACTION);
    	if (StringUtil.isNotEmpty(projectConfigLocation)) {
    		Constant.DEFAULT_CONFIG_NAME = projectConfigLocation;
    	} else if (StringUtil.isNotEmpty(System.getProperty(Constant.PROJECT_CONFIG_NAME))) {
    		Constant.DEFAULT_CONFIG_NAME = System.getProperty(Constant.PROJECT_CONFIG_NAME);
    	}
    	
    	logger.info("{} loading start",Constant.DEFAULT_CONFIG_NAME);
    	//获取DEFAULT_CONFIG_NAME
		String content = PropertyUtils.getPropertiesFileContent(Constant.DEFAULT_CONFIG_NAME);
		ConfHandlerProvider.cache(ConfHandlerEnum.DEFAULT, content, false);
		if (StringUtil.isNotEmpty(System.getProperty(Constant.IP_ADDRESS))) {
			ConfHandlerEnum.DEFAULT.put(Constant.IP_ADDRESS, System.getProperty(Constant.IP_ADDRESS));
		} else {
			ConfHandlerEnum.DEFAULT.put(Constant.IP_ADDRESS, NetUtils.getLocalHost());
		}
		logger.info("{} loading end",Constant.DEFAULT_CONFIG_NAME);
		
		//查询所有systemenv
    	logger.info("systemEnv loading start");
		for (Map.Entry<String, String> entry : EnvironmentUtil.getSystemEnvironment().entrySet()) {
			if (StringUtil.isNotEmpty(entry.getValue())) {
				ConfHandlerEnum.DEFAULT.put(entry.getKey(), entry.getValue());
			}
		}
    	logger.info("systemEnv loading end");
		
		//查询所有systemProperties
    	logger.info("systemProperties loading start");
		for (Map.Entry<String, String> entry : EnvironmentUtil.getSystemProperties().entrySet()) {
			if (StringUtil.isNotEmpty(entry.getValue())) {
				ConfHandlerEnum.DEFAULT.put(entry.getKey(), entry.getValue());
			}
		}
		logger.info("systemProperties loading end");
		
		//扩展的接口
	    for (ConfEndPoint confEndPoint : ConfHandlerProvider.endPoints()) {
	    	confEndPoint.init();
        }
	}
	private static void initExtend() {
		
		//扩展的文件
		String[] extendProperties = ConfHandlerSupport.getExtensionProperties();
		if (extendProperties != null) {
			for (String file : extendProperties) {
				logger.info("{} loading start",file);
				ConfHandlerProvider.load(ConfHandlerEnum.EXTEND, file);//载入缓存
				logger.info("{} loading end",file);
			}
		}
		
		//扩展的接口
	    for (ConfEndPoint confEndPoint : ConfHandlerProvider.endPoints()) {
	    	confEndPoint.loadExtend(ConfHandlerEnum.EXTEND);
        }
	}
	private static void initGlobal() {
		
		//扩展的文件
		String[] globalProperties = ConfHandlerSupport.getGlobalProperties();
		if (globalProperties != null) {
			for (String file : globalProperties) {
				logger.info("{} loading start",file);
				ConfHandlerProvider.load(ConfHandlerEnum.GLOBAL, file);
				logger.info("{} loading start",file);
			}
		}
		
		//扩展的接口
	    for (ConfEndPoint confEndPoint : ConfHandlerProvider.endPoints()) {
	    	confEndPoint.loadGlobal(ConfHandlerEnum.GLOBAL);
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
		String value = ConfHandlerEnum.DEFAULT.get(key);
		if (value != null) {
			return value;
		}
		
		//扩展文件
		value = ConfHandlerEnum.EXTEND.get(key);
		if (value != null) {
			return value;
		}
		
		//全局
		value = ConfHandlerEnum.GLOBAL.get(key);
		if (value != null) {
			return value;
		}
		
		
		//默认值
		if (StringUtil.isNotEmpty(defaultVal)) {
			ConfHandlerEnum.DEFAULT.put(key, defaultVal);
		}
		
		//返回默认值
		return defaultVal;
	}
	
	//在设置应用名称的时候启动各项参数载入
	public static String getAppName() {
		
		String appName = ConfHandlerEnum.DEFAULT.get(Constant.PROJECT_NAME);
		if (StringUtil.isEmpty(appName)) {
			appName = ConfHandlerEnum.DEFAULT.get(Constant.SPRING_BOOT_NAME);
		}
		return appName == null ? "" :appName;
	}

	//获取配置文件
	public static String getConfigValue(String fileName) {
		return ConfHandlerProvider.get(fileName);
	}

	//增加文件是否修改的监听
	public static void addListener(String fileName, ConfListener listener) {
		ConfHandlerProvider.addListener(fileName, listener);
	}

	public static String getConfigRegistryAddress() {
		String config = ConfHandlerEnum.DEFAULT.get(Constant.CONFIG_REGISTRY_ADDRESS_NAME);
		return config == null ? "" :config;
	}
	public static String getNameSpace() {
		String namespace = ConfHandlerEnum.DEFAULT.get(Constant.PROJECR_NAMESPACE_NAME);
		return namespace == null ? "" :namespace;
	}

	public static String getGroup() {
		String group = ConfHandlerEnum.DEFAULT.get(Constant.PROJECR_GROUP_NAME);
		return group == null ? "" :group;
	}
	public static String getNamingRegistryAddress() {
		String naming = ConfHandlerEnum.DEFAULT.get(Constant.NAMING_REGISTRY_ADDRESS_NAME);
		return naming == null ? "" :naming;
	}

}
