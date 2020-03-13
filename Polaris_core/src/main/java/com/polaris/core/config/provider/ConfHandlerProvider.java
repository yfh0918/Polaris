package com.polaris.core.config.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import com.polaris.core.config.Config.Opt;
import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.reader.ConfReaderFactory;
import com.polaris.core.util.StringUtil;
import com.polaris.core.util.UuidUtil;

@SuppressWarnings("rawtypes")
public class ConfHandlerProvider {
	private static final Logger logger = LoggerFactory.getLogger(ConfHandlerProvider.class);
	
    protected final ServiceLoader<ConfHandler> handlerLoader = ServiceLoader.load(ConfHandler.class);

	private volatile AtomicBoolean initialized = new AtomicBoolean(false);
	protected ConfHandler handler;
	
	public void init() {
		initHandler();
		init(Config.EXT);
		init(Config.GLOBAL);
	}
	protected ConfHandler initHandler() {
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


    public void init(String type) {
    	
		//get target files
		String files = type.equals(Config.EXT) ? 
				ConfigFactory.SYSTEM.getProperties(Config.SYSTEM).getProperty(Constant.PROJECT_EXTENSION_PROPERTIES) : 
					ConfigFactory.SYSTEM.getProperties(Config.SYSTEM).getProperty(Constant.PROJECT_GLOBAL_PROPERTIES);
		if (StringUtil.isEmpty(files)) {
			return;
		}
		String[] fileArray = files.split(",");
		
		//target files loop
		for (String file : fileArray) {
			if (!init(type, file)) {
				throw new RuntimeException("type:"+type+ "file:"+file+" is not exsit");
			}
		}
    }
    
    public boolean init(String type, String file) {
    	
		//get config
		Config config = ConfigFactory.get(type);
		
		//get config-center-group
		String group = Config.GLOBAL.equals(type) ? type : ConfClient.getAppName();
		
		//get
		String contents = get(file,group);
		if (StringUtil.isEmpty(contents)) {
			return false;
		}
		
		//config -set
		Properties properties = ConfReaderFactory.get(file).getProperties(contents);
		boolean isUpdate = false;
		String sequence = UuidUtil.generateUuid();
		for (Map.Entry entry : properties.entrySet()) {
			if (onChange(sequence, config, file, entry.getKey(), entry.getValue(), Opt.ADD)) {
				isUpdate = true;
			}
		}
		config.put(file, properties);
		if (isUpdate) {
			onComplete(sequence);
		}
		
		//listen
    	listen(file, group, new ConfHandlerListener() {
			@Override
			public void receive(String content) {
				boolean isUpdate = false;
				Properties oldProperties = config.getProperties(file);
				Properties newProperties = ConfReaderFactory.get(file).getProperties(get(file,group));
				String sequence = UuidUtil.generateUuid();
				for (Map.Entry entry : newProperties.entrySet()) {
					if (!oldProperties.containsKey(entry.getKey())) {
						if (onChange(sequence,config, file, entry.getKey(), entry.getValue(), Opt.ADD)) {
							logger.info("type:{} file:{} key:{} newValue:{} opt:{}", config.getType(),file,entry.getKey(),entry.getValue(),Opt.ADD.name());
							isUpdate = true;
						}
					} else if (!Objects.equals(oldProperties.get(entry.getKey()), newProperties.get(entry.getKey()))) {
						if (onChange(sequence,config, file, entry.getKey(), entry.getValue(), Opt.UPDATE)) {
							logger.info("type:{} file:{} key:{} oldValue:{} newvalue:{} opt:{}", config.getType(),file,entry.getKey(),oldProperties.get(entry.getKey()), entry.getValue(),Opt.UPDATE.name());
							isUpdate = true;
						}
					}
					oldProperties.remove(entry.getKey());
				}
				for (Map.Entry entry : oldProperties.entrySet()) {
					if (onChange(sequence,config, file, entry.getKey(), entry.getValue(), Opt.DELETE)) {
						logger.info("type:{} file:{}, key:{} value:{} opt:{}", config.getType(),file,entry.getKey(),entry.getValue(),Opt.DELETE.name());
						isUpdate = true;
					}
				}
				config.put(file, newProperties);
				if (isUpdate) {
					onComplete(sequence);
				}
			}
		});
    	return true;
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

	public boolean onChange(String sequence, Config config, String file, Object key, Object value, Opt opt) {
		return true;
	}
	public void onComplete(String sequence) {
	}
}
