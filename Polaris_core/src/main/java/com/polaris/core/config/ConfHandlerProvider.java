package com.polaris.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import com.polaris.core.Constant;
import com.polaris.core.OrderWrapper;
import com.polaris.core.config.value.AutoUpdateConfigChangeListener;
import com.polaris.core.util.SpringUtil;
import com.polaris.core.util.StringUtil;

@SuppressWarnings("rawtypes")
public abstract class ConfHandlerProvider {

    private static final ServiceLoader<ConfHandler> handlerLoader = ServiceLoader.load(ConfHandler.class);
	private static final ServiceLoader<ConfEndPoint> endPointLoader = ServiceLoader.load(ConfEndPoint.class);

	private static List<OrderWrapper> configHandlerList = new ArrayList<OrderWrapper>();
	private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
	private static ConfHandler handler = handler();
	
	//初始化
    private static ConfHandler handler() {
		if (!initialized.compareAndSet(false, true)) {
            return handler;
        }
    	for (ConfHandler configHandler : handlerLoader) {
    		OrderWrapper.insertSorted(configHandlerList, configHandler);
        }
    	if (configHandlerList.size() > 0) {
        	handler = (ConfHandler)configHandlerList.get(0).getHandler();
    	}
    	return handler;
    }
    
    public static ServiceLoader<ConfEndPoint> endPoints() {
    	return endPointLoader;
    }
    
    //载入缓存+监听
    public static void load(ConfHandlerEnum configEnum, String fileName) {
    	
		//载入配置到缓存
    	cache(configEnum, get(fileName), false);
		
    	//增加监听
    	addListener(fileName, new ConfListener() {
			@Override
			public void receive(String propertyContent) {
				cache(configEnum, propertyContent, true);
			}
		});
    }
    
    // 载入缓存
    public static void cache(ConfHandlerEnum configEnum, String config, boolean isListen) {
    	if (StringUtil.isNotEmpty(config)) {
			String[] contents = config.split(Constant.LINE_SEP);
			for (String content : contents) {
				String[] keyvalue = ConfHandlerSupport.getKeyValue(content);
				if (keyvalue != null) {
					configEnum.put(keyvalue[0], ConfHandlerSupport.getDecryptValue(keyvalue[1]));
				}
			}
	    	if (isListen) {
		    	SpringUtil.getBean(AutoUpdateConfigChangeListener.class).onChange(configEnum.getCache());//监听配置
	    	}
		} 
    }
	
    // 获取文件的所有内容-扩展
	public static String get(String fileName) {
		
		//扩展点
		if (handler != null) {
			if (ConfHandlerSupport.isGlobal(fileName)) {
				return handler.getConfig(fileName,ConfHandlerEnum.GLOBAL.getType());
			} else {
				return handler.getConfig(fileName,ConfClient.getAppName());
			}
		}
		
    	return null;
	}
	
	// 监听文件内容变化-扩展
	public static void addListener(String fileName, ConfListener listener) {
		
    	//扩展点
		if (handler != null) {
			if (ConfHandlerSupport.isGlobal(fileName)) {
				handler.addListener(fileName, ConfHandlerEnum.GLOBAL.getType(), listener);				
			} else {
				handler.addListener(fileName, ConfClient.getAppName(), listener);
			}
		}
	}
	

}
