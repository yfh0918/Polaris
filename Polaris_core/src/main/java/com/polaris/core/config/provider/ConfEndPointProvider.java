package com.polaris.core.config.provider;

import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

import com.polaris.core.config.ConfEndPoint;

public class ConfEndPointProvider {
    protected final ServiceLoader<ConfEndPoint> endPointLoader = ServiceLoader.load(ConfEndPoint.class);
    private ConfEndPointProvider() {}
    public static ConfEndPointProvider INSTANCE = new ConfEndPointProvider();
    public void init() {
    	for (ConfEndPoint confEndPoint : endPointLoader) {
	    	confEndPoint.init();
        }
    }
    public void filter (String key, String value) {
    	filter (null, key, value);
    }
    public void filter (String file, String key, String value) {
    	for (ConfEndPoint confEndPoint : endPointLoader) {
	    	confEndPoint.filter(file, key, value);
        }
    }
    public void filter (Properties properties) {
    	filter(null, properties);
    }
    public void filter (String file, Properties properties) {
    	for (ConfEndPoint confEndPoint : endPointLoader) {
    		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
    			if (entry.getValue() == null) {
        			confEndPoint.filter(file, entry.getKey().toString(), "");
    			} else {
        			confEndPoint.filter(file, entry.getKey().toString(), entry.getValue().toString());
    			}
    		}
        }
    }
}
