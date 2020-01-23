package com.polaris.container.servlet.listener;

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

public class WSServerListerner implements  ServerListener{
	private static Logger logger = LoggerFactory.getLogger(WSServerListerner.class);
	
	@Override
	public void started(){
		//加载websocket
    	WSEndpointExporter wsEndpointExporter = new WSEndpointExporter();
    	wsEndpointExporter.initServerContainer((ServletContext)(ServerFactory.getServer().getContext()));
	}
	
	public static class WSEndpointExporter {
		
		@javax.annotation.Nullable
		private static ServerContainer serverContainer;
		
		/**
		 * Actually register the endpoints. Called by {@link #afterSingletonsInstantiated()}.
		 */
		private void registerEndpoints() {
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
		
		private void initServerContainer(ServletContext servletContext) {
			serverContainer = (ServerContainer) servletContext.getAttribute("javax.websocket.server.ServerContainer");
			registerEndpoints();
		}
	}
}
