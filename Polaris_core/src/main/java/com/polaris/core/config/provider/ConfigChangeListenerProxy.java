package com.polaris.core.config.provider;

import java.util.ServiceLoader;

import com.polaris.core.config.Config.Opt;
import com.polaris.core.config.ConfigChangeListener;

public class ConfigChangeListenerProxy implements ConfigChangeListener {
    protected final ServiceLoader<ConfigChangeListener> listeners = ServiceLoader.load(ConfigChangeListener.class);
    public static ConfigChangeListener INSTANCE = new ConfigChangeListenerProxy();
    private ConfigChangeListenerProxy() {}
    @Override
    public void onStart(String sequence) {
        for (ConfigChangeListener confEndPoint : listeners) {
            confEndPoint.onStart(sequence);
        }
    }
    @Override
    public void onChange (String sequence, Object key, Object value, Opt opt) {
    	for (ConfigChangeListener confEndPoint : listeners) {
	    	confEndPoint.onChange(sequence, key, value, opt);
        }
    }
    @Override
    public void onComplete(String sequence) {
    	for (ConfigChangeListener confEndPoint : listeners) {
	    	confEndPoint.onComplete(sequence);
        }
    }
}
