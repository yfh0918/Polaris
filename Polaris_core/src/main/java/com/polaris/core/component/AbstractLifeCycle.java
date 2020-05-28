package com.polaris.core.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The AbstractLifeCycle for generic components.
 * {@link LifeCycleManager}
 */
public abstract class AbstractLifeCycle implements LifeCycle{
	final static Logger logger = LoggerFactory.getLogger(AbstractLifeCycle.class);
	
	public static final int FAILED = -1;
    public static final int START = 0;
    public static final int STOP = 1;
    private volatile int _state = STOP;
    private final Object _lock = new Object();
    
	@Override
	public void start() {
		synchronized (_lock) {
			try {
                if (isStart()) {
                    return;
                }
                setStart();
                doStart();
                LifeCycleManager.register(this);
            } catch (Throwable e) {
                setFailed(e);
            }
        }
	}

	@Override
	public void stop()  {
		synchronized (_lock) {
            try {
                if (isStop()) {
                    return;
                }
                setStop();
                doStop();
                LifeCycleManager.unRegister(this);
            } catch (Throwable e) {
                setFailed(e);
            }
        }
	}

	protected abstract void doStart() throws Exception;

    protected abstract void doStop();
    
	public boolean isStart() {
		return _state == START;
	}

	public boolean isStop() {
		return _state == STOP;
	}
	
	public boolean isFailed() {
		return _state == FAILED;
	}
	
	private void setStart() {
		_state = START;
    }
	private void setStop() {
		_state = STOP;
    }
	private void setFailed(Throwable th) {
        _state = FAILED;
        logger.error("FAILED " + this + ": " + th, th);
    }
}
