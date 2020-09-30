package com.polaris.core.component;

import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

public class InitialProxy implements Initial {
    protected final ServiceLoader<Initial> initials = ServiceLoader.load(Initial.class);
    public static Initial INSTANCE = new InitialProxy();
    private volatile AtomicBoolean initialized = new AtomicBoolean(false);
    
    private InitialProxy() {}

    @Override
    public void init() {
        if (!initialized.compareAndSet(false, true)) {
            return;
        }
        for (Initial initial : initials) {
            initial.init();
        }
    }

}
