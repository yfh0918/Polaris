package com.polaris.core.component;

import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AbstractLifeCycle for generic components.
 */
public abstract class AbstractLifeCycle implements LifeCycle {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractLifeCycle.class);
    public static final String STOPPED = "STOPPED";
    public static final String FAILED = "FAILED";
    public static final String STARTING = "STARTING";
    public static final String STARTED = "STARTED";
    public static final String STOPPING = "STOPPING";
    public static final String RUNNING = "RUNNING";

    private final CopyOnWriteArrayList<LifeCycleListener> _listeners = new CopyOnWriteArrayList<LifeCycleListener>();
    private final Object _lock = new Object();
    private static final int STATE_FAILED = -1;
    private static final int STATE_STOPPED = 0;
    private static final int STATE_STARTING = 1;
    private static final int STATE_STARTED = 2;
    private static final int STATE_STOPPING = 3;
    private volatile int _state = STATE_STOPPED;
    private long _stopTimeout = 30000;//graceful stop default 30 seconds

    protected void doStart() throws Exception {
    }

    protected void doStop() throws Exception  {
    }

    @Override
    public final void start() {
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
            }
        }
    }

    @Override
    public final void stop() {
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

    @Override
    public void addLifeCycleListener(LifeCycleListener listener) {
        _listeners.add(listener);
    }

    @Override
    public void removeLifeCycleListener(LifeCycleListener listener) {
        _listeners.remove(listener);
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

    public static String getState(LifeCycle lc) {
        if (lc.isStarting())
            return STARTING;
        if (lc.isStarted())
            return STARTED;
        if (lc.isStopping())
            return STOPPING;
        if (lc.isStopped())
            return STOPPED;
        return FAILED;
    }

    private void setStarted() {
        _state = STATE_STARTED;
        if (LOG.isDebugEnabled()) {
            LOG.debug("started {}", this);
        }
        for (LifeCycleListener listener : _listeners) {
            listener.lifeCycleStarted(this);
        }
    }

    private void setStarting() {
        _state = STATE_STARTING;
        if (LOG.isDebugEnabled()) {
            LOG.debug("starting {}", this);
        }
        for (LifeCycleListener listener : _listeners) {
            listener.lifeCycleStarting(this);
        }
    }

    private void setStopping() {
        _state = STATE_STOPPING;
        if (LOG.isDebugEnabled()) {
            LOG.debug("stopping {}", this);
        }
        for (LifeCycleListener listener : _listeners) {
            listener.lifeCycleStopping(this);
        }
    }

    private void setStopped() {
        _state = STATE_STOPPED;
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} {}", STOPPED, this);
        }
        for (LifeCycleListener listener : _listeners) {
            listener.lifeCycleStopped(this);
        }
    }

    private void setFailed(Throwable th) {
        _state = STATE_FAILED;
        if (LOG.isDebugEnabled()) {
            LOG.warn(FAILED + " " + this + ": " + th, th);
        }
        for (LifeCycleListener listener : _listeners) {
            listener.lifeCycleFailure(this, th);
        }
    }

    public long getStopTimeout()  {
        return _stopTimeout;
    }

    public void setStopTimeout(long stopTimeout) {
        this._stopTimeout = stopTimeout;
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
