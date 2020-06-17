package com.polaris.core.config.provider;

import java.util.ServiceLoader;

import com.polaris.core.config.ConfEndPoint;
import com.polaris.core.config.Config.Opt;

public class ConfEndPointProxy implements ConfEndPoint{
    protected final ServiceLoader<ConfEndPoint> endPointLoader = ServiceLoader.load(ConfEndPoint.class);
    private ConfEndPointProxy() {}
    public static ConfEndPointProxy INSTANCE = new ConfEndPointProxy();
    
    @Override
    public void init() {
    	for (ConfEndPoint confEndPoint : endPointLoader) {
	    	confEndPoint.init();
        }
    }
    @Override
    public void onStart(String sequence) {
        for (ConfEndPoint confEndPoint : endPointLoader) {
            confEndPoint.onStart(sequence);
        }
    }
    @Override
    public void onChange (String sequence, Object key, Object value, Opt opt) {
    	for (ConfEndPoint confEndPoint : endPointLoader) {
	    	confEndPoint.onChange(sequence, key, value, opt);
        }
    }
    @Override
    public void onComplete(String sequence) {
    	for (ConfEndPoint confEndPoint : endPointLoader) {
	    	confEndPoint.onComplete(sequence);
        }
    }
}
