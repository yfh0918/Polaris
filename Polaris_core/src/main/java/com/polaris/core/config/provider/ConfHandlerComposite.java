package com.polaris.core.config.provider;

import java.util.Properties;

import com.polaris.core.config.ConfEndPoint;
import com.polaris.core.config.ConfigChangeListener;
import com.polaris.core.config.Config.Opt;
import com.polaris.core.config.Config.Type;


public class ConfHandlerComposite implements ConfigChangeListener {
    public static final ConfHandlerComposite INSTANCE = new ConfHandlerComposite();
    private static final ConfEndPoint INSTANCE_ENDPOINT = ConfEndPointProxy.INSTANCE;
    private Properties cache = new Properties();
    private ConfHandlerComposite() {}
    
    public void init() {
        ConfHandlerFactory.getOrCreate(Type.SYS);
        ConfHandlerFactory.getOrCreate(Type.EXT);
        ConfHandlerFactory.getOrCreate(Type.GBL);
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
    public void onStart(String sequence) {
        INSTANCE_ENDPOINT.onStart(sequence);
    }
	
	@Override
	public void onChange(String sequence, Object key, Object value, Opt opt) {
		if (opt != Opt.DEL) {
			cache.put(key, value);
		} else {
			cache.remove(key);
		}
		INSTANCE_ENDPOINT.onChange(sequence, key, value ,opt);
	}
	
	@Override
	public void onComplete(String sequence) {
		INSTANCE_ENDPOINT.onComplete(sequence);
	}
}
