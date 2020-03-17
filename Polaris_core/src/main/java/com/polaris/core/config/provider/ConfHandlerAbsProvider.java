package com.polaris.core.config.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import com.polaris.core.OrderWrapper;
import com.polaris.core.config.ConfHandler;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config;
import com.polaris.core.config.ConfigListener;
import com.polaris.core.config.ConfigStrategy;
import com.polaris.core.config.ConfigStrategyFactory;
import com.polaris.core.util.StringUtil;

public abstract class ConfHandlerAbsProvider implements ConfHandlerProvider{
    private static final ServiceLoader<ConfHandler> handlerLoader = ServiceLoader.load(ConfHandler.class);
	private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
	protected static ConfHandler handler;
	protected ConfigStrategy strategy = ConfigStrategyFactory.get();
	private ConfigListener configListener;
	
    @SuppressWarnings("rawtypes")
	protected static ConfHandler initHandler() {
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
    
    @Override
    public void init(ConfigListener configListener) {
    	this.configListener = configListener;
    	initHandler();
    }
    
	public boolean init(String file, String group, Config config) {
    	
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
