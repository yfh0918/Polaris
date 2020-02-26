package com.polaris.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.OrderWrapper;
import com.polaris.core.util.EnvironmentUtil;
import com.polaris.core.util.FileUitl;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.PropertyUtils;
import com.polaris.core.util.StringUtil;

@SuppressWarnings("rawtypes")
public class ConfHandlerProvider {
	private static final Logger logger = LoggerFactory.getLogger(ConfHandlerProvider.class);
	
    protected final ServiceLoader<ConfHandler> handlerLoader = ServiceLoader.load(ConfHandler.class);

	private volatile AtomicBoolean initialized = new AtomicBoolean(false);
	protected ConfHandler handler = handler();
	protected ConfHandler handler() {
		if (!initialized.compareAndSet(false, true)) {
            return handler;
        }
		List<OrderWrapper> configHandlerList = new ArrayList<OrderWrapper>();
    	for (ConfHandler configHandler : handlerLoader) {
    		OrderWrapper.insertSorted(configHandlerList, configHandler);
        }
    	if (configHandlerList.size() > 0) {
        	handler = (ConfHandler)configHandlerList.get(0).getHandler();
    	}
    	return handler;
    }

	
	//初始化操作
	public void init() {
		initSystem();
		init(Config.EXTEND);
		init(Config.GLOBAL);
	}
	public String get(String fileName) {
		return get(fileName, ConfClient.getAppName());
	}
    public String get(String fileName, String group) {
		if (handler != null) {
			return handler.get(fileName, group);
		}
    	return null;
	}
	public void listen(String fileName, ConfListener listerner) {
		listen(fileName, ConfClient.getAppName(), listerner);
	}
    public void listen(String fileName,String group, ConfListener listener) {
		if (handler != null) {
			handler.listen(fileName, group, listener);
		}
	}

    private void initSystem() {
		// 启动字符集
    	System.setProperty(Constant.FILE_ENCODING, Constant.UTF_CODE);

		// 设置application.properties文件名
    	String projectConfigLocation = System.getProperty(Constant.SPRING_CONFIG_LOCACTION);
    	if (StringUtil.isNotEmpty(projectConfigLocation)) {
    		Constant.DEFAULT_CONFIG_NAME = projectConfigLocation;
    	} else if (StringUtil.isNotEmpty(System.getProperty(Constant.PROJECT_CONFIG_NAME))) {
    		Constant.DEFAULT_CONFIG_NAME = System.getProperty(Constant.PROJECT_CONFIG_NAME);
    	}
    	
    	//读取application.properties
    	logger.info("{} load start",Constant.DEFAULT_CONFIG_NAME);
    	for (Map.Entry<Object, Object> entry : PropertyUtils.getProperties(Constant.DEFAULT_CONFIG_NAME).entrySet()) {
			put(ConfigFactory.DEFAULT, entry.getKey().toString(), FileUitl.getDecryptValue(entry.getValue().toString()));
		}
		logger.info("{} load end",Constant.DEFAULT_CONFIG_NAME);
		
		//设置IP地址
		if (StringUtil.isNotEmpty(System.getProperty(Constant.IP_ADDRESS))) {
			ConfigFactory.DEFAULT.put(Constant.IP_ADDRESS, System.getProperty(Constant.IP_ADDRESS));
		} else {
			ConfigFactory.DEFAULT.put(Constant.IP_ADDRESS, NetUtils.getLocalHost());
		}
		
		//设置systemenv
    	logger.info("systemEnv load start");
		for (Map.Entry<String, String> entry : EnvironmentUtil.getSystemEnvironment().entrySet()) {
			if (StringUtil.isNotEmpty(entry.getValue())) {
				put(ConfigFactory.DEFAULT, entry.getKey(), entry.getValue());
			}
		}
    	logger.info("systemEnv load end");
		
		//设置systemProperties
    	logger.info("systemProperties load start");
		for (Map.Entry<String, String> entry : EnvironmentUtil.getSystemProperties().entrySet()) {
			if (StringUtil.isNotEmpty(entry.getValue())) {
				put(ConfigFactory.DEFAULT, entry.getKey(), entry.getValue());
			}
		}
		logger.info("systemProperties load end");
	}
    
    private void init(String type) {
    	
		//获取配置
		Config config = ConfigFactory.get(type);
		
		//应用名称
		String group = Config.GLOBAL.equals(type) ? type : ConfClient.getAppName();
		
		//处理文件
		for (String file : getProperties(type)) {
			//载入配置到缓存
			logger.info("{} load start",file);
			for (Map.Entry<String, String> entry : PropertyUtils.getMap(get(file,group)).entrySet()) {
				put(config, entry.getKey(), entry.getValue());
			}
			logger.info("{} load end",file);
			
	    	//增加监听
			logger.info("{} listen start",file);
	    	listen(file, group, new ConfListener() {
				@Override
				public void receive(String content) {
					for (Map.Entry<String, String> entry : PropertyUtils.getMap(content).entrySet()) {
						put(config, entry.getKey(), entry.getValue());
						listenForPut(config, entry.getKey(), entry.getValue());
					}
				}
			});
			logger.info("{} listen end",file);
		}
    }
    
    /**
	* 设置值
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
    protected void put(Config config, String key, String value) {
    	config.put(key, value);
    }
    /**
	* 设置值时候的监听
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
    protected void listenForPut(Config config, String key, String value){
    }
	
	/**
	* 获取扩展配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	protected String[] getProperties(String type) {
		String files = null;
		if (type.equals(Config.EXTEND)) {
			files = ConfigFactory.DEFAULT.get(Constant.PROJECT_EXTENSION_PROPERTIES);
		} else  if (type.equals(Config.GLOBAL)) {
			files = ConfigFactory.DEFAULT.get(Constant.PROJECT_GLOBAL_PROPERTIES);
		}
		if (StringUtil.isEmpty(files)) {
			return new String[]{};
		}
		return files.split(",");
	}
	

}
