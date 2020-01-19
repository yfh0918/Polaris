package com.polaris.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import com.polaris.core.Constant;
import com.polaris.core.OrderWrapper;
import com.polaris.core.config.value.AutoUpdateConfigChangeListener;
import com.polaris.core.util.SpringUtil;
import com.polaris.core.util.StringUtil;

@SuppressWarnings("rawtypes")
public abstract class ConfigHandlerProvider {

    private static final ServiceLoader<ConfigHandler> serviceLoader = ServiceLoader.load(ConfigHandler.class);
	private static List<OrderWrapper> configHandlerList = new ArrayList<OrderWrapper>();
	private static ConfigHandler handler;
    static {
    	for (ConfigHandler configHandler : serviceLoader) {
    		OrderWrapper.insertSorted(configHandlerList, configHandler);
        }
    	if (configHandlerList.size() > 0) {
        	handler = (ConfigHandler)configHandlerList.get(0).getHandler();
    	}
    }
    
    //载入缓存+监听
    public static void loadConfig(ConfigEnum configEnum, String fileName) {

		//载入配置到缓存
    	cacheConfig(configEnum, getConfig(fileName), false);
		
    	//增加监听
    	addListener(fileName, new ConfListener() {
			@Override
			public void receive(String propertyContent) {
				cacheConfig(configEnum, propertyContent, true);
			}
		});
    }
    
    // 载入缓存
    public static void cacheConfig(ConfigEnum configEnum, String config, boolean isListen) {
    	if (StringUtil.isNotEmpty(config)) {
			String[] contents = config.split(Constant.LINE_SEP);
			for (String content : contents) {
				String[] keyvalue = ConfHandlerSupport.getKeyValue(content);
				if (keyvalue != null) {
					configEnum.put(keyvalue[0], keyvalue[1]);
				}
			}
	    	if (isListen) {
		    	SpringUtil.getBean(AutoUpdateConfigChangeListener.class).onChange(configEnum.getCache());//监听配置
	    	}
		} 
    }
	
    // 获取文件的所有内容-扩展
	public static String getConfig(String fileName) {
		//扩展点
		if (handler != null) {
			if (ConfHandlerSupport.isGlobal(fileName)) {
				return handler.getConfig(fileName,ConfigEnum.GLOBAL.getType());
			} else {
				return handler.getConfig(fileName,ConfClient.getAppName());
			}
		}
		
    	return null;
	}
	
	// 监听文件内容变化-扩展
	public static void addListener(String fileName, ConfListener listener) {
		if (handler != null) {
			if (ConfHandlerSupport.isGlobal(fileName)) {
				handler.addListener(fileName, ConfigEnum.GLOBAL.getType(), listener);				
			} else {
				handler.addListener(fileName, ConfClient.getAppName(), listener);
			}
		}
	}
	
	
	

}
