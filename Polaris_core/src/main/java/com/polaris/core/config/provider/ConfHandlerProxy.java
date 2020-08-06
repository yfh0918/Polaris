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
    protected ConfigChangeListener[] configChangeListeners;
    protected Type type;
	protected ConfigChangeNotifier notifier = ConfigChangeNotifierFactory.get();
	public ConfHandlerProxy(Type type, ConfigChangeListener... configChangeListeners) {
	    this.type = type;
	    this.configChangeListeners = configChangeListeners;
	}
	
	@Override
	public void init() {
        
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
        
        //get target files from system properties
        String files = ConfigFactory.get(Type.SYS).getProperties(Type.SYS.name()).getProperty(type.getPropertyType());
        if (StringUtil.isEmpty(files)) {
            return;
        }
        
        //target files loop
        String[] fileArray = files.split(",");
        for (String fileName : fileArray) {
            if (getAndListen(fileName, type.getGroup()) == null) {
                throw new ConfigException("type:"+type.name()+" file:"+fileName+" is not exsit");
            }
        }
    }
	
	@Override
    public String getAndListen(String fileName,String group, ConfHandlerListener... listener) {
    	
		//get
		String contents = get(fileName,group);
		if (StringUtil.isNotEmpty(contents)) {
			notifier.notify(ConfigFactory.get(type), fileName, contents,configChangeListeners);
		}
		
		//listen
    	listen(fileName, group, new ConfHandlerListener() {
			@Override
			public void receive(String contents) {
			    notifier.notify(ConfigFactory.get(type), fileName, contents,configChangeListeners);
			}
		});
    	
    	return contents;
    }

	@Override
    public String get(String fileName, String group) {
		if (handler != null) {
			return handler.get(fileName, group);
		}
    	return null;
	}
	
	@Override
    public void listen(String fileName,String group, ConfHandlerListener listener) {
		if (handler != null) {
			handler.listen(fileName, group, listener);
		}
	}
}
