package com.polaris.core.config.provider;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.config.Config;
import com.polaris.core.config.Config.Opt;
import com.polaris.core.config.ConfigChangeException;
import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.ConfigListener;


public class ConfCompositeProvider implements ConfigListener {
	private static final Logger logger = LoggerFactory.getLogger(ConfCompositeProvider.class);
    public static final ConfCompositeProvider INSTANCE = new ConfCompositeProvider();
    private static final ConfHandlerProvider INSTANCE_SYSTEM = ConfHandlerProviderFactory.get(Config.SYSTEM);
    private static final ConfHandlerProvider INSTANCE_EXT = ConfHandlerProviderFactory.get(Config.EXT);
    private static final ConfHandlerProvider INSTANCE_GLOBAL = ConfHandlerProviderFactory.get(Config.GLOBAL);
    private static final ConfEndPointProvider INSTANCE_ENDPOINT = ConfEndPointProvider.INSTANCE;
    private Properties cache = new Properties();
    private ConfCompositeProvider() {}
    
    public void init() {
    	INSTANCE_SYSTEM.init(this);
    	INSTANCE_EXT.init(this);
    	INSTANCE_GLOBAL.init(this);
    	INSTANCE_ENDPOINT.init();
    }
	public String getProperty(String key, String... defaultValue) {
		if (defaultValue == null || defaultValue.length == 0) {
			return cache.getProperty(key);
		}
		return cache.getProperty(key,defaultValue[0]);
	}
	public Properties getProperties() {
		return cache;
	}
	public void putProperty(Object key, Object value) {
		cache.put(key, value);
	}
	
	@Override
	public void onChange(String sequence, Config config, String file, Object key, Object value, Opt opt) {
		//优先级-ext
		if (config == ConfigFactory.get(Config.EXT)) {
			if (ConfigFactory.get(Config.SYSTEM).contain(key)) {
				logger.warn("type:{} file:{}, key:{} value:{} opt:{} failed ,"
						+ "caused by conflicted with system properties ", config.getType(),file,key,value,opt.name());
				throw new ConfigChangeException("conflicted with system properties");
			}
		}
		
		//优先级-global
		if (config == ConfigFactory.get(Config.GLOBAL)) {
			if (ConfigFactory.get(Config.SYSTEM).contain(key)) {
				logger.warn("type:{} file:{}, key:{} value:{} opt:{} failed ,"
						+ "caused by conflicted with system properties", config.getType(),file,key,value,opt.name());
				throw new ConfigChangeException("conflicted with system properties");
			}
			if (ConfigFactory.get(Config.EXT).contain(key)) {
				logger.warn("type:{} file:{}, key:{} value:{} opt:{} failed ,"
						+ "caused by conflicted with ext properties", config.getType(),file,key,value,opt.name());
				throw new ConfigChangeException("conflicted with ext properties");
			}
		}
		
		//update cache
		if (opt != Opt.DELETE) {
			cache.put(key, value);
		} else {
			cache.remove(key);
		}
		
		//notiy endpoint
		INSTANCE_ENDPOINT.onChange(sequence, config,file,key.toString(), value == null ? null: value.toString(),opt);
	}
	
	@Override
	public void onComplete(String sequence) {
		INSTANCE_ENDPOINT.onComplete(sequence);
	}
}
