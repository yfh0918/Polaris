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
	public void putProperty(String key, String value) {
		ConfigFactory.SYSTEM.getProperties(Config.DEFAULT).put(key, value);
		cache(Config.DEFAULT, key, value);
	}
	
	@Override
    protected void putProperties(Config config, String file, Properties properties) {
		super.putProperties(config, file, properties);
		cacheByPriority(config, file, properties);
    }
	
	@Override
	public void putPropertiesFromListen(Config config, String file,Properties properties) {
		super.putPropertiesFromListen(config, file, properties);
		cacheByPriority(config, file, properties);
		SpringUtil.getBean(SpringAutoUpdateConfigChangeListener.class).onChange(properties);
	}
	
	private void cacheByPriority(Config config, String file, Properties properties) {
		//优先级-system最高
		if (config == ConfigFactory.SYSTEM) {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				cache(file, entry.getKey(), entry.getValue());
			}
			return;
		}
		
		//优先级-ext
		if (config == ConfigFactory.EXT) {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				if (ConfigFactory.SYSTEM.contain(entry.getKey())) {
					continue;
				}
				cache(file, entry.getKey(), entry.getValue());
			}
			return;
		}
		
		//优先级-global
		if (config == ConfigFactory.GLOBAL) {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				if (ConfigFactory.SYSTEM.contain(entry.getKey()) || ConfigFactory.EXT.contain(entry.getKey())) {
					continue;
				}
				cache(file, entry.getKey(), entry.getValue());
			}
			return;
		}
	}
	
	private void cache(String file, Object key, Object value) {
		cache.put(key, value);
		INSTANCE_ENDPOINT.filter(file, key.toString(), value == null ? "":value.toString());
	}
}
