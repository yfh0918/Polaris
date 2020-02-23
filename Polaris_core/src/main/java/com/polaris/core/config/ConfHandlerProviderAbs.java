package com.polaris.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import com.polaris.core.OrderWrapper;

@SuppressWarnings("rawtypes")
public abstract class ConfHandlerProviderAbs {
	
    protected final ServiceLoader<ConfHandler> handlerLoader = ServiceLoader.load(ConfHandler.class);
    protected final ServiceLoader<ConfEndPoint> endPointLoader = ServiceLoader.load(ConfEndPoint.class);

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
		
		//初始DEFAULT_CONFIG
		initDefault();
		
		//初始化外部模块接入点
		initEndPoint();
		
		//载入扩展文件
		initHandler();
	}
	
	protected abstract void initDefault();
    
    protected void initEndPoint() {
	    for (ConfEndPoint confEndPoint : endPointLoader) {
	    	confEndPoint.init();
        }
    }
    
    protected void initHandler() {
		initHandler(Config.EXTEND);
		initHandler(Config.GLOBAL);
    }

    protected abstract void initHandler(String type);

    public String get(String fileName, String group) {
		
		//扩展点
		if (handler != null) {
			return handler.get(fileName, group);
		}
		
    	return null;
	}
	
    public void listen(String fileName,String group, ConfListener listener) {
		
    	//扩展点
		if (handler != null) {
			handler.listen(fileName, group, listener);
		}
	}
	
	public String get(String fileName) {
		return get(fileName, ConfClient.getAppName());
	}
	
	public void listen(String fileName, ConfListener listerner) {
		listen(fileName, ConfClient.getAppName(), listerner);
	}
	
	public Map<String, String> get(Config config) {
		return config.get();
	}
	public String get(Config config,String key) {
		return config.get(key);
	}
	public void put(Config config, String content) {
		config.put(content);
	}
	public void put(Config config, String key, String value) {
		config.put(key, value);
		for (ConfEndPoint confEndPoint : endPointLoader) {
	    	confEndPoint.filter(key, value);
        }
	}
}
