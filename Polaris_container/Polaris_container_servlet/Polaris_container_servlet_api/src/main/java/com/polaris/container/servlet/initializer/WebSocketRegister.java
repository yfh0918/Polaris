package com.polaris.container.servlet.initializer;

import java.util.Map;

import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

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
        
        @javax.annotation.Nullable
        private ServerContainer serverContainer;

        @Override
        public void started(LifeCycle event) {
            if (event instanceof Server) {
                springContext = ((Server)event).getContext();
                servletContext = ServletContextHelper.getServletContext(springContext);
                types = new Class<?>[] {ServerEndpoint.class,ServerEndpointConfig.class};
                if (servletContext != null) {
                    serverContainer = (ServerContainer) servletContext.getAttribute("javax.websocket.server.ServerContainer");
                    init();
                }
            }
        }
        
        @Override
        protected void doRegister(Class<?> type, Map<String, Object> attributes, ScannedGenericBeanDefinition beanDefinition) {
            try {
                if (type == ServerEndpoint.class) {
                    serverContainer.addEndpoint(Class.forName(beanDefinition.getBeanClassName()));
                } else if (type == ServerEndpointConfig.class){
                    serverContainer.addEndpoint((ServerEndpointConfig)(Class.forName(beanDefinition.getBeanClassName()).newInstance()));
                }
            } catch (DeploymentException ex) {
                throw new IllegalStateException("Failed to register @ServerEndpoint class: " + beanDefinition.getBeanClassName(), ex);
            } catch (Exception e) {
                throw new ServletContextException(beanDefinition.getBeanClassName() + " is not found");
            }
        }
    }
}

