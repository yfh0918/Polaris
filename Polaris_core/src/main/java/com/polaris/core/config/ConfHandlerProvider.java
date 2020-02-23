package com.polaris.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.OrderWrapper;
import com.polaris.core.config.value.AutoUpdateConfigChangeListener;
import com.polaris.core.util.SpringUtil;

@SuppressWarnings("rawtypes")
public abstract class ConfHandlerProvider {

	private static final Logger logger = LoggerFactory.getLogger(ConfHandlerProvider.class);
	
    private static final ServiceLoader<ConfHandler> handlerLoader = ServiceLoader.load(ConfHandler.class);
	private static final ServiceLoader<ConfEndPoint> endPointLoader = ServiceLoader.load(ConfEndPoint.class);

	private static List<OrderWrapper> configHandlerList = new ArrayList<OrderWrapper>();
	private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
	private static ConfHandler handler = handler();
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
    
    protected static void initEndPoint() {
	    for (ConfEndPoint confEndPoint : endPointLoader) {
	    	confEndPoint.init();
        }
    }
    
    protected static void filterEndPoint(String key, String value) {
	    for (ConfEndPoint confEndPoint : endPointLoader) {
	    	confEndPoint.filter(key, value);
        }
    }
    
    protected static void initHandler() {
    	
    	//扩展的文件
		String[] extendProperties = ConfHandlerSupport.getExtensionProperties();
		if (extendProperties != null) {
			for (String file : extendProperties) {
				logger.info("{} loading start",file);
				initHandler(ConfHandlerEnum.EXTEND, file);//载入缓存
				logger.info("{} loading end",file);
			}
		}
		
		//扩展的文件
		String[] globalProperties = ConfHandlerSupport.getGlobalProperties();
		if (globalProperties != null) {
			for (String file : globalProperties) {
				logger.info("{} loading start",file);
				initHandler(ConfHandlerEnum.GLOBAL, file);
				logger.info("{} loading start",file);
			}
		}
    }
    
    //初始化
    protected static void initHandler(ConfHandlerEnum configEnum, String fileName) {
    	
		//载入配置到缓存
    	configEnum.put(get(fileName));
		
    	//增加监听
    	listen(fileName, new ConfListener() {
			@Override
			public void receive(String config) {
				configEnum.put(config);
		    	SpringUtil.getBean(AutoUpdateConfigChangeListener.class).onChange(configEnum.getCache());//监听配置
			}
		});
    }
    
    // 获取文件的所有内容-扩展
	public static String get(String fileName) {
		
		//扩展点
		if (handler != null) {
			if (ConfHandlerSupport.isGlobal(fileName)) {
				return handler.get(fileName,ConfHandlerEnum.GLOBAL.getType());
			} else {
				return handler.get(fileName,ConfClient.getAppName());
			}
		}
		
    	return null;
	}
	
	// 监听文件内容变化-扩展
	public static void listen(String fileName, ConfListener listener) {
		
    	//扩展点
		if (handler != null) {
			if (ConfHandlerSupport.isGlobal(fileName)) {
				handler.listen(fileName, ConfHandlerEnum.GLOBAL.getType(), listener);				
			} else {
				handler.listen(fileName, ConfClient.getAppName(), listener);
			}
		}
	}
}
