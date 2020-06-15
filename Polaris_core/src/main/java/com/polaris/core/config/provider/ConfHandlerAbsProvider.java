package com.polaris.core.config.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import com.polaris.core.OrderWrapper;
import com.polaris.core.config.ConfHandler;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.ConfigListener;
import com.polaris.core.exception.ConfigException;
import com.polaris.core.util.StringUtil;

@SuppressWarnings("rawtypes")
public abstract class ConfHandlerAbsProvider implements ConfHandlerProvider{
    private final ServiceLoader<ConfHandler> handlerLoader = ServiceLoader.load(ConfHandler.class);
	private volatile AtomicBoolean initialized = new AtomicBoolean(false);
	protected ConfHandlerStrategy strategy = ConfHandlerStrategyFactory.get();
	private ConfigListener configListener;
	
	private ConfHandler handler;
	private ConfHandler initHandler() {
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
    
    protected void init(ConfigListener configListener, Type type, String propertyType) {
        this.configListener = configListener;
        
        //initial config handler
        initHandler();
        
        //get target files from system properties
        String files = ConfigFactory.get(Type.SYS).getProperties(Type.SYS.name()).getProperty(propertyType);
        if (StringUtil.isEmpty(files)) {
            return;
        }
        String[] fileArray = files.split(",");
        
        //target files loop
        for (String file : fileArray) {
            if (!getAndListen(file)) {
                throw new ConfigException("type:"+type.name()+" file:"+file+" is not exsit");
            }
        }
    }
    
	public boolean getAndListen(String file, String group, Config config) {
    	
		//get
		String contents = get(file,group);
		if (StringUtil.isEmpty(contents)) {
			return false;
		}
		
		//get
		strategy.notify(configListener, config, file, contents);
		
		//listen
    	listen(file, group, new ConfHandlerListener() {
			@Override
			public void receive(String contents) {
				strategy.notify(configListener, config, file, contents);
			}
		});
    	return true;
    }

    public String get(String fileName, String group) {
		if (handler != null) {
			return handler.get(fileName, group);
		}
    	return null;
	}
    
    public void listen(String fileName,String group, ConfHandlerListener listener) {
		if (handler != null) {
			handler.listen(fileName, group, listener);
		}
	}
}
