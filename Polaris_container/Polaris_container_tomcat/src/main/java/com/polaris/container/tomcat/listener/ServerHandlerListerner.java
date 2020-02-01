package com.polaris.container.tomcat.listener;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.listener.ServerListener;
import com.polaris.container.listener.ServerListenerSupport;
import com.polaris.container.servlet.listener.WebsocketListerner;

/**
 * Class Name : ServerHandler
 * Description : 服务器Handler
 * Creator : yufenghua
 * Modifier : yufenghua
 *
 */

public class ServerHandlerListerner implements LifecycleListener{
	
	private static final Logger logger = LoggerFactory.getLogger(ServerHandlerListerner.class);
	private ServerListener websocketListerner = new WebsocketListerner();
	
	public ServerHandlerListerner () {
	}

	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		// Process the event that has occurred
        if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
        } else if (event.getType().equals(Lifecycle.BEFORE_START_EVENT)) {
        	websocketListerner.starting();
        	ServerListenerSupport.starting();
        	logger.info("TomcatServer启动中！");
        } else if (event.getType().equals(Lifecycle.AFTER_START_EVENT)) {
        	websocketListerner.started();
			ServerListenerSupport.started();
        	logger.info("TomcatServer启动成功！");
        } else if (event.getType().equals(Lifecycle.BEFORE_STOP_EVENT)) {
        	websocketListerner.stopping();
			ServerListenerSupport.stopping();
        	logger.info("TomcatServer停止中！");
        } else if (event.getType().equals(Lifecycle.AFTER_STOP_EVENT)) {
        	websocketListerner.stopped();
			ServerListenerSupport.stopped();
        	logger.info("TomcatServer已经停止！");
        } else if (event.getType().equals(Lifecycle.AFTER_DESTROY_EVENT)) {
        }
		
	}
	
    
}
