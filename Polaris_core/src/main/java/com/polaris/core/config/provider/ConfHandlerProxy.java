package com.polaris.core.config.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import com.polaris.core.OrderWrapper;
import com.polaris.core.config.ConfHandler;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.ConfigChangeListener;
import com.polaris.core.config.ConfigChangeNotifier;
import com.polaris.core.exception.ConfigException;
import com.polaris.core.util.StringUtil;

@SuppressWarnings("rawtypes")
public class ConfHandlerProxy implements ConfHandler{
    
    //global-var
    private static final ServiceLoader<ConfHandler> handlerLoader = ServiceLoader.load(ConfHandler.class);
    private volatile AtomicBoolean initialized = new AtomicBoolean(false);
    private static ConfHandler handler;

    //instance-var
    private ConfigChangeListener[] configChangeListeners;
    private Type type;
    private ConfigChangeNotifier notifier = ConfigChangeNotifierFactory.get();
	public ConfHandlerProxy(Type type, ConfigChangeListener... configChangeListeners) {
	    this.type = type;
	    this.configChangeListeners = configChangeListeners;
	    init();
	}
	
	private void init() {
        
        //initial config handler
	    if (!initialized.compareAndSet(false, true)) {
            return;
        }
        List<OrderWrapper> configHandlerList = new ArrayList<OrderWrapper>();
        for (ConfHandler configHandler : handlerLoader) {
            OrderWrapper.insertSorted(configHandlerList, configHandler);
        }
        if (configHandlerList.size() > 0) {
            handler = (ConfHandler)configHandlerList.get(0).getHandler();
        }
        if (handler == null) {
            throw new ConfigException("Excepiton caused by ConfHandler is null");
        }
        
    }
	
	@Override
    public String getAndListen(String group,String fileName, ConfHandlerListener... listener) {
    	
		//get
		String contents = get(group, fileName);
		if (StringUtil.isNotEmpty(contents)) {
			notifier.notify(ConfigFactory.get(type), group, fileName, contents,configChangeListeners);
		}
		
		//listen
    	listen(group, fileName, new ConfHandlerListener() {
			@Override
			public void receive(String contents) {
			    notifier.notify(ConfigFactory.get(type), group,fileName,  contents,configChangeListeners);
			}
		});
    	
    	return contents;
    }

	@Override
    public String get(String group, String fileName) {
		if (handler != null) {
			return handler.get(group, fileName);
		}
    	return null;
	}
	
	@Override
    public void listen(String group, String fileName,ConfHandlerListener listener) {
		if (handler != null) {
			handler.listen(group, fileName, listener);
		}
	}
}
