package com.polaris.core.component;

import java.util.EventListener;

public interface LifeCycleListener extends EventListener {
    void lifeCycleStarting(LifeCycle event);

    void lifeCycleStarted(LifeCycle event);

    void lifeCycleFailure(LifeCycle event, Throwable cause);

    void lifeCycleStopping(LifeCycle event);

    void lifeCycleStopped(LifeCycle event);
}
