package com.polaris.core.component;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The ManagedComponent for generic components.
 */
public abstract class ManagedComponent extends LifeCycleWithListenerManager implements LifeCycleListener{
	final static Logger logger = LoggerFactory.getLogger(ManagedComponent.class);
	
    private static final CopyOnWriteArrayList<ManagedComponent> _managedComponents = new CopyOnWriteArrayList<>();

    /**
     * The constructor for protected.
     */
	protected ManagedComponent() {
        addLifeCycleListener(this);
    }
	
	@Override
	public void lifeCycleStarted(LifeCycle component) {
		if (component instanceof ManagedComponent) {
			if (_managedComponents.contains((ManagedComponent)component)) {
	    		return;
	    	}
	    	logger.debug("ManagedComponent add lifeCycle:{}",component.getClass().getName());
	    	_managedComponents.add((ManagedComponent)component);
		}
	}

	@Override
	public void lifeCycleStopped(LifeCycle component) {
		if (component instanceof ManagedComponent) {
			logger.debug("ManagedComponent remove component:{}",component.getClass().getName());
	    	_managedComponents.remove((ManagedComponent)component);
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
	

}