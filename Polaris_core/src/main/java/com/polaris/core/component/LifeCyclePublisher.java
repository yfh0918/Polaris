package com.polaris.core.component;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class LifeCyclePublisher extends AbstractLifeCycle {
    protected final CopyOnWriteArrayList<LifeCycleListener> _listeners = new CopyOnWriteArrayList<LifeCycleListener>();
    public void addLifeCycleListener(LifeCycleListener listener) {
        _listeners.add(listener);
    };
    public void removeLifeCycleListener(LifeCycleListener listener) {
        _listeners.remove(listener);
    };
}
