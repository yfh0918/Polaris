package com.polaris.core.config.provider;

import java.util.Map;
import java.util.Objects;
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
	public String getProperty(String key, String... defaultValue) {
		if (defaultValue == null || defaultValue.length == 0) {
			return cache.getProperty(key);
		}
		return cache.getProperty(key,defaultValue[0]);
	}
	public void putProperty(String key, String value) {
		ConfigFactory.SYSTEM.getProperties(Config.SYSTEM).put(key, value);
		cache(Config.SYSTEM, Config.SYSTEM, key, value, true);
	}
	
	@Override
    protected void putProperties(Config config, String file, Properties properties, boolean fromListen) {
		super.putProperties(config, file, properties,fromListen);
		cacheByPriority(config, file, properties, fromListen);
    }
	
	private void cacheByPriority(Config config, String file, Properties properties, boolean fromListen) {
		//优先级-system最高
		if (config == ConfigFactory.SYSTEM) {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				cache(config.getType(), file, entry.getKey(), entry.getValue(),fromListen);
			}
			return;
		}
		
		//优先级-ext
		if (config == ConfigFactory.EXT) {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				if (ConfigFactory.SYSTEM.contain(entry.getKey())) {
					continue;
				}
				cache(config.getType(), file, entry.getKey(), entry.getValue(),fromListen);
			}
			return;
		}
		
		//优先级-global
		if (config == ConfigFactory.GLOBAL) {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				if (ConfigFactory.SYSTEM.contain(entry.getKey()) || ConfigFactory.EXT.contain(entry.getKey())) {
					continue;
				}
				cache(config.getType(), file, entry.getKey(), entry.getValue(),fromListen);
			}
			return;
		}
	}
	
	private void cache(String type, String file, Object key, Object value, boolean fromListen) {
		if (!fromListen) {
			cache.put(key, value);
			INSTANCE_ENDPOINT.put(type, file, key.toString(), value == null ? null:value.toString());
		} else {
			String orgValue = cache.getProperty(key.toString());
			if (!Objects.equals(orgValue, value)) {
				cache.put(key, value);
				INSTANCE_ENDPOINT.put(type, file, key.toString(), value == null ? null:value.toString());
				SpringUtil.getBean(SpringAutoUpdateConfigChangeListener.class).onChange(key.toString());
			}
		}
	}
}
