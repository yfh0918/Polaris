package com.polaris.container.tomcat.listener;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

import com.polaris.container.listener.ServerListenerSupport;

/**
 * Class Name : ServerHandler
 * Description : 服务器Handler
 * Creator : yufenghua
 * Modifier : yufenghua
 *
 */

public class ServerHandlerListerner implements LifecycleListener{
	
	public ServerHandlerListerner () {
	}

	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		// Process the event that has occurred
        if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
        } else if (event.getType().equals(Lifecycle.BEFORE_START_EVENT)) {
        	ServerListenerSupport.starting();
        } else if (event.getType().equals(Lifecycle.AFTER_START_EVENT)) {
			ServerListenerSupport.started();
        } else if (event.getType().equals(Lifecycle.BEFORE_STOP_EVENT)) {
			ServerListenerSupport.stopping();
        } else if (event.getType().equals(Lifecycle.AFTER_STOP_EVENT)) {
			ServerListenerSupport.stopped();
        } else if (event.getType().equals(Lifecycle.AFTER_DESTROY_EVENT)) {
        }
		
	}
	
    
}
