package com.polaris.core.component;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The AbstractLifeCycle for generic components.
 * {@link LifeCycleManager}
 */
public abstract class ManagedComponent extends AbstractLifeCycleWithListener{
	final static Logger logger = LoggerFactory.getLogger(ManagedComponent.class);
	
    private static final CopyOnWriteArrayList<ManagedComponent> _managedComponents = new CopyOnWriteArrayList<>();

	@Override
	public void lifeCycleStarted(LifeCycle component) {
		if (component instanceof ManagedComponent) {
			ManagedComponent.add((ManagedComponent)component);
		}
	}

	@Override
	public void lifeCycleStopped(LifeCycle component) {
		if (component instanceof ManagedComponent) {
			ManagedComponent.remove((ManagedComponent)component);
		}
	}

	
	public static void init() {
		Iterator<ManagedComponent> iterator = _managedComponents.iterator();
		while (iterator.hasNext()) {
			ManagedComponent component = iterator.next();
			logger.debug("ManagedComponent start component:{}",component.getClass().getName());
			component.start();
		}
	}
	
	public static void destroy() {
		Iterator<ManagedComponent> iterator = _managedComponents.iterator();
		while (iterator.hasNext()) {
			ManagedComponent component = iterator.next();
	    	logger.debug("ManagedComponent stop component:{}",component.getClass().getName());
	    	component.stop();
		}
	}
	
    public static void add(ManagedComponent comonent) {
    	if (_managedComponents.contains(comonent)) {
    		return;
    	}
    	logger.debug("ManagedComponent add lifeCycle:{}",comonent.getClass().getName());
    	_managedComponents.add(comonent);
	}
    
    public static void remove(ManagedComponent component) {
    	logger.debug("ManagedComponent remove component:{}",component.getClass().getName());
    	_managedComponents.remove(component);
	}
}