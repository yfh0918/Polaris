package com.polaris.core.config.provider;

import java.util.Properties;

import com.polaris.core.config.Config.Opt;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.ConfigChangeListener;

public class ConfHandlerComposite implements ConfigChangeListener{
    public static final ConfHandlerComposite INSTANCE = new ConfHandlerComposite();
    private Properties cache = new Properties();
    private ConfHandlerComposite() {}
    
    public void init() {
        ConfHandlerFactory.getOrCreate(Type.SYS, this, ConfEndPointProxy.INSTANCE).init();
        ConfHandlerFactory.getOrCreate(Type.EXT, this, ConfEndPointProxy.INSTANCE).init();
        ConfHandlerFactory.getOrCreate(Type.GBL, this, ConfEndPointProxy.INSTANCE).init();
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
	public void onChange(String sequence, Object key, Object value, Opt opt) {
		if (opt != Opt.DEL) {
			cache.put(key, value);
		} else {
			cache.remove(key);
		}
	}
}
