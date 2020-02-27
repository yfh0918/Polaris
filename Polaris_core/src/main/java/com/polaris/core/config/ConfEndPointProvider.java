package com.polaris.core.config;

import java.util.ServiceLoader;

public class ConfEndPointProvider {
    protected final ServiceLoader<ConfEndPoint> endPointLoader = ServiceLoader.load(ConfEndPoint.class);
    public void init() {
    	for (ConfEndPoint confEndPoint : endPointLoader) {
	    	confEndPoint.init();
        }
    }
    public void filter (String key, String value) {
    	for (ConfEndPoint confEndPoint : endPointLoader) {
	    	confEndPoint.filter(key, value);
        }
    }
}
