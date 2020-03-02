package com.polaris.core.config.provider;

import java.util.Map;
import java.util.Properties;

import com.polaris.core.config.Config;
import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.value.SpringAutoUpdateConfigChangeListener;
import com.polaris.core.util.SpringUtil;


public class ConfCompositeProvider extends ConfHandlerProvider {

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

	public String getProperty(String key) {
		return cache.getProperty(key);
	}
	public void put(String key, String value) {
		cache.put(key, value);
	}
	
	public void put(Config config, String key, String value) {
		put(config, Config.DEFAULT, key, value);
	}
	@Override
	public void put(Config config, String file, String key, String value) {
		super.put(config, file, key, value);
		cache(config, key, value);
		INSTANCE_ENDPOINT.filter(file, key, value);
	}

    protected void put(Config config, Properties properties) {
		put(config, Config.DEFAULT, properties);
	}
	@Override
    protected void put(Config config, String file, Properties properties) {
		super.put(config, file, properties);
		cache(config, properties);
		INSTANCE_ENDPOINT.filter(file, properties);
    }
	
	@Override
	public void listenReceive(Config config, String file,Properties properties) {
		super.listenReceive(config, file, properties);
		SpringUtil.getBean(SpringAutoUpdateConfigChangeListener.class).onChange(properties);
	}
	
	private void cache(Config config, Properties properties) {
		//优先级-system最高
		if (config == ConfigFactory.SYSTEM) {
			cache.putAll(properties);
			return;
		}
		
		//优先级-ext
		if (config == ConfigFactory.EXT) {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				if (ConfigFactory.SYSTEM.contain(entry.getKey())) {
					continue;
				}
				cache.put(entry.getKey(), entry.getValue());
			}
			return;
		}
		
		//优先级-global
		if (config == ConfigFactory.GLOBAL) {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				if (ConfigFactory.SYSTEM.contain(entry.getKey()) || ConfigFactory.EXT.contain(entry.getKey())) {
					continue;
				}
				cache.put(entry.getKey(), entry.getValue());
			}
			return;
		}
	}
	private void cache(Config config, Object key, Object value) {
		
		//优先级-system最高
		if (config == ConfigFactory.SYSTEM) {
			cache.put(key, value);
			return;
		}
		
		//优先级-ext
		if (config == ConfigFactory.get(Config.EXT)) {
			if (ConfigFactory.SYSTEM.contain(key)) {
				return;
			}
			cache.put(key, value);
			return;
		}
		
		//优先级-global
		if (config == ConfigFactory.GLOBAL) {
			if (ConfigFactory.SYSTEM.contain(key)) {
				return;
			}
			if (ConfigFactory.EXT.contain(key)) {
				return;
			}
			cache.put(key, value);
			return;
		}
	}
	
}
