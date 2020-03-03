package com.polaris.core.config.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.OrderWrapper;
import com.polaris.core.config.ConfClient;
import com.polaris.core.config.ConfHandler;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config;
import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.reader.ConfReaderFactory;
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

	public void init() {
		init(Config.EXT);
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
	public void listen(String fileName, ConfHandlerListener listerner) {
		listen(fileName, ConfClient.getAppName(), listerner);
	}
    public void listen(String fileName,String group, ConfHandlerListener listener) {
		if (handler != null) {
			handler.listen(fileName, group, listener);
		}
	}

    private void init(String type) {
    	
		//get config
		Config config = ConfigFactory.get(type);
		
		//get config-center-group
		String group = Config.GLOBAL.equals(type) ? type : ConfClient.getAppName();
		
		//get target files
		String files = type.equals(Config.EXT) ? 
				ConfigFactory.SYSTEM.getProperties(Config.DEFAULT).getProperty(Constant.PROJECT_EXTENSION_PROPERTIES) : 
					ConfigFactory.SYSTEM.getProperties(Config.DEFAULT).getProperty(Constant.PROJECT_GLOBAL_PROPERTIES);
		if (StringUtil.isEmpty(files)) {
			return;
		}
		String[] fileArray = files.split(",");
		
		//target files loop
		for (String file : fileArray) {
			//load to config container
			logger.info("{} load start",file);
			putProperties(config, file, ConfReaderFactory.get(file).getProperties(get(file,group)));
			logger.info("{} load end",file);
			
			logger.info("{} listen start",file);
	    	listen(file, group, new ConfHandlerListener() {
				@Override
				public void receive(String content) {
					putPropertiesFromListen(config, file, ConfReaderFactory.get(file).getProperties(content));
				}
			});
			logger.info("{} listen end",file);
		}
    }
    
    /**
	* config-put
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
    protected void putProperties(Config config, String file, Properties properties) {
    	config.put(file, properties);
    }
    
    /**
	* listen-from config-center
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
    protected void putPropertiesFromListen(Config config, String file, Properties properties){
    	config.put(file, properties);
    }
	
}
