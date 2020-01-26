package com.polaris.container.listener;

import javax.servlet.ServletContext;
import javax.websocket.server.ServerContainer;

import com.polaris.container.ServerFactory;
import com.polaris.container.websocket.WSEndpointExporter;

public class WSServerListerner implements  ServerListener{
	
	@Override
	public void started(){
		//加载websocket
    	WSEndpointExporter wsEndpointExporter = new WSEndpointExporter();
    	ServletContext servletContext = (ServletContext)(ServerFactory.getServer().getContext());
    	ServerContainer serverContainer = (ServerContainer) servletContext.getAttribute("javax.websocket.server.ServerContainer");
    	wsEndpointExporter.registerEndpoints(serverContainer);
	}
	
	
}
