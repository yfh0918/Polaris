package com.polaris.core.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AbstractLifeCycle for generic components.
 */
public abstract class AbstractLifeCycle implements LifeCycle ,LifeCycleState{
    private static final Logger LOG = LoggerFactory.getLogger(AbstractLifeCycle.class);

    private final Object _lock = new Object();
    private volatile int _state = STATE_STOPPED;

    protected void doStart() throws Exception {
    }

    protected void doStop() throws Exception  {
    }

    @Override
    public final void start() throws Exception{
        synchronized (_lock) {
            try {
                if (_state == STATE_STARTED || _state == STATE_STARTING) {
                    return;
                }
                setStarting();
                doStart();
                setStarted();
            } catch (Throwable e) {
                setFailed(e);
                throw e;
            }
        }
    }

    @Override
    public final void stop() throws Exception{
        synchronized (_lock) {
            try {
                if (_state == STATE_STOPPING || _state == STATE_STOPPED) {
                    return;
                }
                setStopping();
                doStop();
                setStopped();
            } catch (Throwable e) {
                setFailed(e);
                throw e;
            }
        }
    }

    @Override
    public boolean isRunning()
    {
        final int state = _state;

        return state == STATE_STARTED || state == STATE_STARTING;
    }

    @Override
    public boolean isStarted() {
        return _state == STATE_STARTED;
    }

    @Override
    public boolean isStarting() {
        return _state == STATE_STARTING;
    }

    @Override
    public boolean isStopping() {
        return _state == STATE_STOPPING;
    }

    @Override
    public boolean isStopped() {
        return _state == STATE_STOPPED;
    }

    @Override
    public boolean isFailed() {
        return _state == STATE_FAILED;
    }

    public String getState()  {
        switch (_state)
        {
            case STATE_FAILED:
                return FAILED;
            case STATE_STARTING:
                return STARTING;
            case STATE_STARTED:
                return STARTED;
            case STATE_STOPPING:
                return STOPPING;
            case STATE_STOPPED:
                return STOPPED;
        }
        return null;
    }

    protected void setStarted() {
        _state = STATE_STARTED;
        if (LOG.isDebugEnabled()) {
            LOG.debug("started {}", this);
        }
    }

    protected void setStarting() {
        _state = STATE_STARTING;
        if (LOG.isDebugEnabled()) {
            LOG.debug("starting {}", this);
        }
    }

    protected void setStopping() {
        _state = STATE_STOPPING;
        if (LOG.isDebugEnabled()) {
            LOG.debug("stopping {}", this);
        }
    }

    protected void setStopped() {
        _state = STATE_STOPPED;
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} {}", STOPPED, this);
        }
    }

    protected void setFailed(Throwable th) {
        _state = STATE_FAILED;
        if (LOG.isDebugEnabled()) {
            LOG.warn(FAILED + " " + this + ": " + th, th);
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
