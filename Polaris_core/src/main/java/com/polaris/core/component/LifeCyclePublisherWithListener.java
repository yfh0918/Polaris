package com.polaris.core.component;

import java.util.Iterator;

/**
 * The LifeCycleWithListenerManager for generic components.
 */
public abstract class LifeCyclePublisherWithListener extends LifeCyclePublisher implements LifeCycleListener{
    
    protected LifeCyclePublisherWithListener() {
        addLifeCycleListener(this);
    }
    
    @Override
    protected void setStarting() {
    	super.setStarting();
    	Iterator<LifeCycleListener> iterator = _listeners.iterator();
		while (iterator.hasNext()) {
			LifeCycleListener listener = iterator.next();
			listener.starting(this);
		}
    }
    
    @Override
    protected void setStarted() {
    	super.setStarted();
    	Iterator<LifeCycleListener> iterator = _listeners.iterator();
		while (iterator.hasNext()) {
			LifeCycleListener listener = iterator.next();
			listener.started(this);
		}
    }
    
    @Override
    protected void setStopping() {
    	super.setStopping();
    	Iterator<LifeCycleListener> iterator = _listeners.iterator();
		while (iterator.hasNext()) {
			LifeCycleListener listener = iterator.next();
			listener.stopping(this);
		}
    }
    
    @Override
    protected void setStopped() {
    	super.setStopped();
    	Iterator<LifeCycleListener> iterator = _listeners.iterator();
		while (iterator.hasNext()) {
			LifeCycleListener listener = iterator.next();
			listener.stopped(this);
			removeLifeCycleListener(listener);
		}
    }
    
    @Override
    protected void setFailed(Throwable th) {
    	super.setFailed(th);
    	Iterator<LifeCycleListener> iterator = _listeners.iterator();
		while (iterator.hasNext()) {
			LifeCycleListener listener = iterator.next();
			listener.failure(this, th);
		}
    }
}
