package com.polaris.core.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AbstractLifeCycle for generic components.
 */
public abstract class AbstractLifeCycle implements LifeCycle {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractLifeCycle.class);

    private final Object _lock = new Object();
    private volatile int _state = LifeCycleState.STATE_STOPPED;

    protected void doStart() throws Exception {
    }

    protected void doStop() throws Exception  {
    }

    @Override
    public final void start() {
        synchronized (_lock) {
            try {
                if (_state == LifeCycleState.STATE_STARTED || _state == LifeCycleState.STATE_STARTING) {
                    return;
                }
                setStarting();
                doStart();
                setStarted();
            } catch (Throwable e) {
                setFailed(e);
            }
        }
    }

    @Override
    public final void stop() {
        synchronized (_lock) {
            try {
                if (_state == LifeCycleState.STATE_STOPPING || _state == LifeCycleState.STATE_STOPPED) {
                    return;
                }
                setStopping();
                doStop();
                setStopped();
            } catch (Throwable e) {
                setFailed(e);
            }
        }
    }

    @Override
    public boolean isRunning()
    {
        final int state = _state;

        return state == LifeCycleState.STATE_STARTED || state == LifeCycleState.STATE_STARTING;
    }

    @Override
    public boolean isStarted() {
        return _state == LifeCycleState.STATE_STARTED;
    }

    @Override
    public boolean isStarting() {
        return _state == LifeCycleState.STATE_STARTING;
    }

    @Override
    public boolean isStopping() {
        return _state == LifeCycleState.STATE_STOPPING;
    }

    @Override
    public boolean isStopped() {
        return _state == LifeCycleState.STATE_STOPPED;
    }

    @Override
    public boolean isFailed() {
        return _state == LifeCycleState.STATE_FAILED;
    }

    public String getState()  {
        switch (_state)
        {
            case LifeCycleState.STATE_FAILED:
                return LifeCycleState.FAILED;
            case LifeCycleState.STATE_STARTING:
                return LifeCycleState.STARTING;
            case LifeCycleState.STATE_STARTED:
                return LifeCycleState.STARTED;
            case LifeCycleState.STATE_STOPPING:
                return LifeCycleState.STOPPING;
            case LifeCycleState.STATE_STOPPED:
                return LifeCycleState.STOPPED;
        }
        return null;
    }

    protected void setStarted() {
        _state = LifeCycleState.STATE_STARTED;
        if (LOG.isDebugEnabled()) {
            LOG.debug("started {}", this);
        }
    }

    protected void setStarting() {
        _state = LifeCycleState.STATE_STARTING;
        if (LOG.isDebugEnabled()) {
            LOG.debug("starting {}", this);
        }
    }

    protected void setStopping() {
        _state = LifeCycleState.STATE_STOPPING;
        if (LOG.isDebugEnabled()) {
            LOG.debug("stopping {}", this);
        }
    }

    protected void setStopped() {
        _state = LifeCycleState.STATE_STOPPED;
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} {}", LifeCycleState.STOPPED, this);
        }
    }

    protected void setFailed(Throwable th) {
        _state = LifeCycleState.STATE_FAILED;
        if (LOG.isDebugEnabled()) {
            LOG.warn(LifeCycleState.FAILED + " " + this + ": " + th, th);
        }
    }

    @Override
    public String toString() {
        Class<?> clazz = getClass();
        String name = clazz.getSimpleName();
        if ((name == null || name.length() == 0) && clazz.getSuperclass() != null) {
            clazz = clazz.getSuperclass();
            name = clazz.getSimpleName();
        }
        return String.format("%s@%x{%s}", name, hashCode(), getState());
    }
}
