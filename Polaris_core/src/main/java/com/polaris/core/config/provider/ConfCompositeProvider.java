package com.polaris.core.config.provider;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.config.Config;
import com.polaris.core.config.Config.Opt;
import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.ConfigListener;


public class ConfCompositeProvider implements ConfigListener {
	private static final Logger logger = LoggerFactory.getLogger(ConfCompositeProvider.class);
    public static final ConfCompositeProvider INSTANCE = new ConfCompositeProvider();
    private static final ConfSystemHandlerProvider INSTANCE_SYSTEM = ConfSystemHandlerProvider.INSTANCE;
    private static final ConfHandlerProvider INSTANCE_EXT = ConfHandlerProvider.INSTANCE;
    private static final ConfEndPointProvider INSTANCE_ENDPOINT = ConfEndPointProvider.INSTANCE;
    private Properties cache = new Properties();
    private ConfCompositeProvider() {}
    
    public void init() {
    	INSTANCE_SYSTEM.init(this);
    	INSTANCE_EXT.init(this);
    	INSTANCE_ENDPOINT.init(this);
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
	public boolean onChange(String sequence, Config config, String file, Object key, Object value, Opt opt) {
		//优先级-system最高
		if (config == ConfigFactory.SYSTEM) {
			onChange(sequence,key, value, opt);
		}
		
		//优先级-ext
		if (config == ConfigFactory.EXT) {
			if (ConfigFactory.SYSTEM.contain(key)) {
				logger.warn("type:{} file:{}, key:{} value:{} opt:{} failed ,"
						+ "caused by conflicted with system properties ", config.getType(),file,key,value,opt.name());
				return false;
			}
			onChange(sequence,key, value, opt);
		}
		
		//优先级-global
		if (config == ConfigFactory.GLOBAL) {
			if (ConfigFactory.SYSTEM.contain(key)) {
				logger.warn("type:{} file:{}, key:{} value:{} opt:{} failed ,"
						+ "caused by conflicted with system properties", config.getType(),file,key,value,opt.name());
				return false;
			}
			if (ConfigFactory.EXT.contain(key)) {
				logger.warn("type:{} file:{}, key:{} value:{} opt:{} failed ,"
						+ "caused by conflicted with ext properties", config.getType(),file,key,value,opt.name());
				return false;
			}
			onChange(sequence,key, value, opt);
		}
		return true;
	}
	
	private void onChange(String sequence, Object key, Object value, Opt opt) {
		if (opt != Opt.DELETE) {
			cache.put(key, value);
		} else {
			cache.remove(key);
		}
		INSTANCE_ENDPOINT.onChange(sequence, key.toString(), value == null ? null: value.toString(),opt);
	}
	
	@Override
	public void onComplete(String sequence) {
		INSTANCE_ENDPOINT.onComplete(sequence);
	}
}
