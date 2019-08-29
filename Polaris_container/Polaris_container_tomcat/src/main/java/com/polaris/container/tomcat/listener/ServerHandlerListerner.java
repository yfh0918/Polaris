package com.polaris.container.tomcat.listener;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.http.supports.ServerListener;

/**
 * Class Name : ServerHandler
 * Description : 服务器Handler
 * Creator : yufenghua
 * Modifier : yufenghua
 *
 */

public class ServerHandlerListerner implements LifecycleListener{
	
	private static final Logger logger = LoggerFactory.getLogger(ServerHandlerListerner.class);
	private ServerListener serverlistener;
	
	public ServerHandlerListerner (ServerListener serverlistener) {
		this.serverlistener = serverlistener;
	}

	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		// Process the event that has occurred
        if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
        } else if (event.getType().equals(Lifecycle.BEFORE_START_EVENT)) {
        } else if (event.getType().equals(Lifecycle.AFTER_START_EVENT)) {
        	serverlistener.started();
        	logger.info("TomcatServer启动成功！");
            // Restore docBase for management tools
         } else if (event.getType().equals(Lifecycle.CONFIGURE_STOP_EVENT)) {
        } else if (event.getType().equals(Lifecycle.AFTER_INIT_EVENT)) {
        } else if (event.getType().equals(Lifecycle.AFTER_DESTROY_EVENT)) {
        }
		
	}
	
    
}
