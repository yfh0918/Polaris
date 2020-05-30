package com.polaris.core.component;

import java.util.EventListener;

public interface LifeCycleListener extends EventListener {
    default void lifeCycleStarting(LifeCycle event) {};

    default void lifeCycleStarted(LifeCycle event){};

    default void lifeCycleFailure(LifeCycle event, Throwable cause){};

    default void lifeCycleStopping(LifeCycle event){};

    default void lifeCycleStopped(LifeCycle event){};
}
