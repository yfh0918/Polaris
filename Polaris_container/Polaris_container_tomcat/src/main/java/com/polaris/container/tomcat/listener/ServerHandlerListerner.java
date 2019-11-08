package com.polaris.container.tomcat.listener;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.http.initializer.WSEndpointExporter;
import com.polaris.http.listener.ServerListener;

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
	private StandardContext standardContext;
	
	public ServerHandlerListerner (ServerListener serverlistener, StandardContext standardContext) {
		this.serverlistener = serverlistener;
		this.standardContext = standardContext; 
	}

	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		// Process the event that has occurred
        if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
        } else if (event.getType().equals(Lifecycle.BEFORE_START_EVENT)) {
        	serverlistener.starting(standardContext.getServletContext());
        	logger.info("TomcatServer启动中！");
        } else if (event.getType().equals(Lifecycle.AFTER_START_EVENT)) {
        	
        	//启动外部
        	serverlistener.started(standardContext.getServletContext());
        	
        	//加载websocket
        	WSEndpointExporter wsEndpointExporter = new WSEndpointExporter();
        	wsEndpointExporter.initServerContainer(standardContext.getServletContext());
        	
        	//日志
        	logger.info("TomcatServer启动成功！");
        } else if (event.getType().equals(Lifecycle.BEFORE_STOP_EVENT)) {
        	serverlistener.stopping(standardContext.getServletContext());
        	logger.info("TomcatServer停止中！");
        } else if (event.getType().equals(Lifecycle.AFTER_STOP_EVENT)) {
        	serverlistener.stopped(standardContext.getServletContext());
        	logger.info("TomcatServer已经停止！");
        } else if (event.getType().equals(Lifecycle.AFTER_DESTROY_EVENT)) {
        }
		
	}
	
    
}
