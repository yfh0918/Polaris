package com.polaris.core.config.provider;

import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import com.polaris.core.config.ConfEndPoint;
import com.polaris.core.config.Config.Opt;

public class ConfEndPointProxy implements ConfEndPoint{
    protected final ServiceLoader<ConfEndPoint> endPointLoader = ServiceLoader.load(ConfEndPoint.class);
    public static ConfEndPoint INSTANCE = new ConfEndPointProxy();
    private volatile AtomicBoolean initialized = new AtomicBoolean(false);
    
    private ConfEndPointProxy() {}

    @Override
    public void init() {
        if (!initialized.compareAndSet(false, true)) {
            return;
        }
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
