package com.polaris.container.listener;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import com.polaris.container.ServerFactory;
import com.polaris.core.util.SpringUtil;

public class WebsocketListerner implements  ServerListener{
	
	@Override
	public void started() {
		//加载websocket
    	WSEndpointExporter wsEndpointExporter = new WSEndpointExporter();
    	ServletContext servletContext = (ServletContext)(ServerFactory.getServer().getContext());
    	ServerContainer serverContainer = (ServerContainer) servletContext.getAttribute("javax.websocket.server.ServerContainer");
    	wsEndpointExporter.registerEndpoints(serverContainer);
	}
	
	public static class WSEndpointExporter implements  ServerListener{
		private static Logger logger = LoggerFactory.getLogger(WSEndpointExporter.class);
		
		@javax.annotation.Nullable
		private static ServerContainer serverContainer;
		
		/**
		 * Actually register the endpoints. Called by {@link #afterSingletonsInstantiated()}.
		 */
		public void registerEndpoints(ServerContainer serverContainer) {
			WSEndpointExporter.serverContainer = serverContainer;
			Set<Class<?>> endpointClasses = new LinkedHashSet<>();


			ApplicationContext context = SpringUtil.getApplicationContext();
			if (context != null) {
				String[] endpointBeanNames = context.getBeanNamesForAnnotation(ServerEndpoint.class);
				for (String beanName : endpointBeanNames) {
					endpointClasses.add(context.getType(beanName));
				}
			}

			for (Class<?> endpointClass : endpointClasses) {
				registerEndpoint(endpointClass);
			}

			if (context != null) {
				Map<String, ServerEndpointConfig> endpointConfigMap = context.getBeansOfType(ServerEndpointConfig.class);
				for (ServerEndpointConfig endpointConfig : endpointConfigMap.values()) {
					registerEndpoint(endpointConfig);
				}
			}
		}

		public static void registerEndpoint(Class<?> endpointClass) {
			Assert.state(serverContainer != null,
					"No ServerContainer set. Most likely the server's own WebSocket ServletContainerInitializer " +
					"has not run yet. Was the Spring ApplicationContext refreshed through a " +
					"org.springframework.web.context.ContextLoaderListener, " +
					"i.e. after the ServletContext has been fully initialized?");
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Registering @ServerEndpoint class: " + endpointClass);
				}
				serverContainer.addEndpoint(endpointClass);
			}
			catch (DeploymentException ex) {
				throw new IllegalStateException("Failed to register @ServerEndpoint class: " + endpointClass, ex);
			}
		}

		public static void registerEndpoint(ServerEndpointConfig endpointConfig) {
			Assert.state(serverContainer != null, "No ServerContainer set");
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Registering ServerEndpointConfig: " + endpointConfig);
				}
				serverContainer.addEndpoint(endpointConfig);
			}
			catch (DeploymentException ex) {
				throw new IllegalStateException("Failed to register ServerEndpointConfig: " + endpointConfig, ex);
			}
		}	
	}
}
