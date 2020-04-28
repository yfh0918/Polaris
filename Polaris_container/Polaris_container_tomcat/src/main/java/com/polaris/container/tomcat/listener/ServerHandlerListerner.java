package com.polaris.container.tomcat.listener;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

import com.polaris.container.listener.ServerListenerHelper;

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
        	ServerListenerHelper.starting();
        } else if (event.getType().equals(Lifecycle.AFTER_START_EVENT)) {
			ServerListenerHelper.started();
        } else if (event.getType().equals(Lifecycle.BEFORE_STOP_EVENT)) {
			ServerListenerHelper.stopping();
        } else if (event.getType().equals(Lifecycle.AFTER_STOP_EVENT)) {
			ServerListenerHelper.stopped();
        } else if (event.getType().equals(Lifecycle.AFTER_DESTROY_EVENT)) {
        }
		
	}
	
    
}
