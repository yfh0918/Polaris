package com.polaris.core.notify;

import java.util.EventListener;
import java.util.concurrent.Executor;

public class AbstractEventListener implements EventListener{
    private Executor executor;

    public AbstractEventListener() {
        EventPublisher.addEventListener(this);
    }
    
    public AbstractEventListener(Executor executor) {
        this();
        this.executor = executor;
    }

    public Executor getExecutor() {
        return executor;
    }
    
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
}
