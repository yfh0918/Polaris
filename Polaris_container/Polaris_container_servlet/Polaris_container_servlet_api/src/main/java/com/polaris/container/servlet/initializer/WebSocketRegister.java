package com.polaris.container.servlet.initializer;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

import com.polaris.container.Server;
import com.polaris.container.listener.ServerListener;
import com.polaris.container.listener.ServerListenerExtension;
import com.polaris.core.component.LifeCycle;
import com.polaris.core.exception.ServletContextException;

public class WebSocketRegister implements ServerListenerExtension {

    @Override
    public ServerListener[] getServerListeners() {
        return new ServerListener[] {new WSEndpointExporter()};
    }

    public static class WSEndpointExporter extends ComponentScanRegister implements ServerListener{
        private static Logger logger = LoggerFactory.getLogger(WSEndpointExporter.class);
        
        @javax.annotation.Nullable
        private static ServerContainer serverContainer;
        private static Set<Class<?>> endpointClassSet = new LinkedHashSet<>();
        private static Set<ServerEndpointConfig> endpointConfigSet = new LinkedHashSet<>();

        @Override
        public void started(LifeCycle event) {
            if (event instanceof Server) {
                springContext = ((Server)event).getContext();
                servletContext = ServletContextHelper.getServletContext(springContext);
                annotationType = ServerEndpoint.class;
                if (servletContext != null) {
                    serverContainer = (ServerContainer) servletContext.getAttribute("javax.websocket.server.ServerContainer");
                    init();
                    registerEndpoints(serverContainer);
                }
            }
        }
        
        @Override
        protected void doRegister(Map<String, Object> attributes, ScannedGenericBeanDefinition beanDefinition) {
            try {
                registerEndpoint(Class.forName(beanDefinition.getBeanClassName()));
            } catch (ClassNotFoundException e) {
                throw new ServletContextException(beanDefinition.getBeanClassName() + " is not found");
            }
        }
        
        /**
         * Actually register the endpoints. Called by {@link #afterSingletonsInstantiated()}.
         */
        public void registerEndpoints(ServerContainer serverContainer) {
            for (Class<?> endpointClass : endpointClassSet) {
                registerEndpoint(endpointClass);
            }
            endpointClassSet.clear();
            for (ServerEndpointConfig endpointConfig : endpointConfigSet) {
                registerEndpoint(endpointConfig);
            }
            endpointConfigSet.clear();
        }

        public static void registerEndpoint(Class<?> endpointClass) {
            if (serverContainer == null) {
                endpointClassSet.add(endpointClass);
                return;
            }
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
            if (serverContainer == null) {
                endpointConfigSet.add(endpointConfig);
                return;
            }
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

