package com.polaris.core.component;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ManagedComponent for generic components.
 * {@link ManagedComponentListener}
 */
public abstract class ManagedComponent extends LifeCyclePublisherWithListener {
	final static Logger logger = LoggerFactory.getLogger(ManagedComponent.class);
	
    private static final CopyOnWriteArrayList<ManagedComponent> _managedComponents = new CopyOnWriteArrayList<>();

	@Override
	public void started(LifeCycle component) {
		if (component instanceof ManagedComponent) {
			if (_managedComponents.contains((ManagedComponent)component)) {
	    		return;
	    	}
	    	logger.debug("ManagedComponent add lifeCycle:{}",component.getClass().getName());
	    	_managedComponents.add((ManagedComponent)component);
		}
	}

	@Override
	public void stopped(LifeCycle component) {
		if (component instanceof ManagedComponent) {
			logger.debug("ManagedComponent remove component:{}",component.getClass().getName());
	    	_managedComponents.remove((ManagedComponent)component);
		}
	}

	/**
	 * called by ManagedComponentListener of ServerListenerHelper.
	 */
	public static void init() {
		Iterator<ManagedComponent> iterator = _managedComponents.iterator();
		while (iterator.hasNext()) {
			ManagedComponent component = iterator.next();
			logger.debug("ManagedComponent start component:{}",component.getClass().getName());
			try {
				component.start();
			} catch (Exception e) {
				logger.error("ManagedComponent start component :{} is error:{}",component.getClass().getName(),e);
			}
		}
	}
	
	/**
	 * called by ManagedComponentListener of ServerListenerHelper.
	 */
	public static void destroy() {
		Iterator<ManagedComponent> iterator = _managedComponents.iterator();
		while (iterator.hasNext()) {
			ManagedComponent component = iterator.next();
	    	logger.debug("ManagedComponent stop component:{}",component.getClass().getName());
	    	try {
				component.stop();
			} catch (Exception e) {
				logger.error("ManagedComponent stop component :{} is error:{}",component.getClass().getName(),e);
			}
		}
	}
	

}