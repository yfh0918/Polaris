package com.polaris.core.component;

public interface LifeCycleListenerManager {
    default void addLifeCycleListener(LifeCycleListener listener) {};
    default void removeLifeCycleListener(LifeCycleListener listener) {};
}
