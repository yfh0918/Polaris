package com.polaris.core.config.provider;

import java.util.Properties;

import com.polaris.core.config.ConfEndPoint;
import com.polaris.core.config.Config.Opt;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.ConfigListener;


public class ConfCompositeProvider implements ConfigListener {
    public static final ConfCompositeProvider INSTANCE = new ConfCompositeProvider();
    private static final ConfHandlerProvider INSTANCE_SYS = ConfHandlerProviderFactory.get(Type.SYS);
    private static final ConfHandlerProvider INSTANCE_EXT = ConfHandlerProviderFactory.get(Type.EXT);
    private static final ConfHandlerProvider INSTANCE_GBL = ConfHandlerProviderFactory.get(Type.GBL);
    private static final ConfEndPoint INSTANCE_ENDPOINT = ConfEndPointProvider.INSTANCE;
    private Properties cache = new Properties();
    private ConfCompositeProvider() {}
    
    public void init() {
    	INSTANCE_SYS.init(this);
    	INSTANCE_EXT.init(this);
    	INSTANCE_GBL.init(this);
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
