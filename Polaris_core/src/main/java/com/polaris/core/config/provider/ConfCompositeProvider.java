package com.polaris.core.config.provider;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.config.Config;
import com.polaris.core.config.Config.Opt;
import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.value.SpringAutoUpdateConfigChangeListener;
import com.polaris.core.util.SpringUtil;


public class ConfCompositeProvider extends ConfHandlerProvider {
	private static final Logger logger = LoggerFactory.getLogger(ConfCompositeProvider.class);
    public static final ConfCompositeProvider INSTANCE = new ConfCompositeProvider();
    private static final ConfSystemHandlerProvider INSTANCE_SYSTEM = ConfSystemHandlerProvider.INSTANCE;
    private static final ConfEndPointProvider INSTANCE_ENDPOINT = ConfEndPointProvider.INSTANCE;
    private Properties cache = new Properties();
    private ConfCompositeProvider() {}
    
    @Override
    public void init() {
    	INSTANCE_SYSTEM.init(this);
    	INSTANCE_ENDPOINT.init(this);
    	super.init();
    }
	public String getProperty(String key, String... defaultValue) {
		if (defaultValue == null || defaultValue.length == 0) {
			return cache.getProperty(key);
		}
		return cache.getProperty(key,defaultValue[0]);
	}
	public void putProperty(Object key, Object value) {
		ConfigFactory.SYSTEM.put(Config.SYSTEM, key, value);
		putProperty(ConfigFactory.SYSTEM, Config.SYSTEM, key, value, Opt.ADD);
	}
	
	@Override
	public void putProperty(Config config, String file, Object key, Object value, Opt opt) {
		super.putProperty(config, file, key, value, opt);
		//优先级-system最高
		if (config == ConfigFactory.SYSTEM) {
			onChange(key, value, opt);
		}
		
		//优先级-ext
		if (config == ConfigFactory.EXT) {
			if (ConfigFactory.SYSTEM.contain(key)) {
				logger.warn("type:{} file:{}, key:{} value:{} opt:{} failed ,"
						+ "caused by conflicted with system properties ", config.getType(),file,key,value,opt.name());
				return;
			}
			onChange(key, value, opt);
		}
		
		//优先级-global
		if (config == ConfigFactory.GLOBAL) {
			if (ConfigFactory.SYSTEM.contain(key)) {
				logger.warn("type:{} file:{}, key:{} value:{} opt:{} failed ,"
						+ "caused by conflicted with system properties", config.getType(),file,key,value,opt.name());
				return;
			}
			if (ConfigFactory.EXT.contain(key)) {
				logger.warn("type:{} file:{}, key:{} value:{} opt:{} failed ,"
						+ "caused by conflicted with ext properties", config.getType(),file,key,value,opt.name());
				return;
			}
			onChange(key, value, opt);
		}
	}
	
	private void onChange(Object key, Object value, Opt opt) {
		if (opt != Opt.DELETE) {
			cache.put(key, value);
		} else {
			cache.remove(key);
		}
		INSTANCE_ENDPOINT.onChange(key.toString(), value == null ? null: value.toString(),opt);
		SpringAutoUpdateConfigChangeListener listener = SpringUtil.getBean(SpringAutoUpdateConfigChangeListener.class);
		if (listener != null) {
			listener.onChange(key.toString());
		}
	}
}
