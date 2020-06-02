package com.polaris.core.component;

import java.util.EventListener;

public interface LifeCycleListener extends EventListener {
    default void starting(LifeCycle event) {};

    default void started(LifeCycle event){};

    default void failure(LifeCycle event, Throwable cause){};

    default void stopping(LifeCycle event){};

    default void stopped(LifeCycle event){};
}
