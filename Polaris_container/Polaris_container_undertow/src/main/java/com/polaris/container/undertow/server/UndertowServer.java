package com.polaris.container.undertow.server;

import java.util.Collections;
import java.util.ServiceLoader;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.polaris.container.ServerOrder;
import com.polaris.container.SpringContextServer;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.api.ServletStackTraces;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;

/**
 * Class Name : UndertowServer
 * Description : Undertow服务器
 * Creator : yufenghua
 * Modifier : yufenghua
 */
@Order(ServerOrder.UNDERTOW)
public class UndertowServer extends SpringContextServer {
    private static final Logger logger = LoggerFactory.getLogger(UndertowServer.class);
    
    private Undertow undertow;
    private DeploymentManager manager;
    private String serverPort;
    private String contextPath;
	private final ServiceLoader<ServletContainerInitializer> serviceLoader = ServiceLoader.load(ServletContainerInitializer.class);

    /**
     * servlet上下文
     */
    private ServletContext servletContext;

    /**
     * 服务器初始化
     * @throws ServletException 
     */
    private void init() throws ServletException {
    	
    	//create context();
    	contextPath = createContextPath();
    	
    	//create deployment manager
    	manager = createDeploymentManager();
    	
    	//create builder
    	serverPort = ConfClient.get(Constant.SERVER_PORT_NAME, Constant.SERVER_PORT_DEFAULT_VALUE);
    	Builder builder = createBuilder(Integer.parseInt(serverPort));
    	
    	//create httpHander
    	HttpHandler httpHandler = manager.start();
    	PathHandler path = Handlers.path(Handlers.redirect(contextPath))
                .addPrefixPath(contextPath, httpHandler);
    	
    	//path set
		builder.setHandler(path);
		
		//build server
		undertow =  builder.build();
    }
    private Builder createBuilder(int port) {
		Builder builder = Undertow.builder();
		builder.addHttpListener(port, "0.0.0.0");
		String bufferSize = ConfClient.get("server.bufferSize");
		if (bufferSize != null) {
			builder.setBufferSize(Integer.parseInt(bufferSize));
		}
		String ioThreads = ConfClient.get("server.ioThreads");
		if (ioThreads != null) {
			builder.setIoThreads(Integer.parseInt(ioThreads));
		}
		String workerThreads = ConfClient.get("server.workerThreads");
		if (workerThreads != null) {
			builder.setWorkerThreads(Integer.parseInt(workerThreads));
		}
		String directBuffers = ConfClient.get("server.directBuffers");
		if (directBuffers != null) {
			builder.setDirectBuffers(Boolean.parseBoolean(directBuffers));
		}
		Integer shutdownTimeout = Integer.parseInt(ConfClient.get("server.shutdownTimeout","10"));
		builder.setServerOption(UndertowOptions.SHUTDOWN_TIMEOUT, shutdownTimeout);
		return builder;
	}

    private DeploymentManager createDeploymentManager() {
		DeploymentInfo deployment = Servlets.deployment();
        deployment.setClassLoader(UndertowServer.class.getClassLoader());
        deployment.setContextPath(contextPath);
		deployment.setDisplayName(ConfClient.getAppName());
		deployment.setDeploymentName("Polaris");
		deployment.setServletStackTraces(ServletStackTraces.NONE);
		String eagerInitFilters = ConfClient.get("server.eagerInitFilters");
		if (eagerInitFilters != null) {
			deployment.setEagerFilterInit(Boolean.parseBoolean(eagerInitFilters));
		}
        for (ServletContainerInitializer servletContainerInitializer : serviceLoader) {
    		deployment.addServletContainerInitializer(new ServletContainerInitializerInfo(servletContainerInitializer.getClass(),Collections.emptySet()));
		}
        WebSocketDeploymentInfo info = new WebSocketDeploymentInfo();
        deployment.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, info);
		
        //add deployment
		DeploymentManager deployManager = Servlets.newContainer().addDeployment(deployment);
		deployManager.deploy();
		
		//session
		SessionManager sessionManager = deployManager.getDeployment().getSessionManager();
		int sessionTimeout = Integer.parseInt(ConfClient.get("server.sessionTimeout",String.valueOf(30 * 60)));
		sessionManager.setDefaultSessionTimeout(sessionTimeout);//30mins
		
		//servlet context
    	servletContext = deployManager.getDeployment().getServletContext();
		return deployManager;
	}
    
    private String createContextPath() {
    	String contextPath =ConfClient.get(Constant.SERVER_CONTEXT,"/"); 
        if (!contextPath.startsWith("/")) {
        	contextPath = "/" + contextPath;
        }
        return contextPath;
    }
    
    /**
     * 启动服务器
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
    	//如果已经启动就先停掉
        if (this.undertow != null) {
            this.undertow.stop();
            this.undertow = null;
        }

        //没有初始化过，需要重新初始化
        if (this.undertow == null) {
            init();
        }
    	
        //启动服务
        this.undertow.start();
        
        //log
        logger.info("Undertow started on port(s) " + this.serverPort + " with context path '" + this.contextPath + "'");
    }
    
    
    /**
     * 停止服务服务器
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        super.stop();
    	manager.stop();
    	manager.undeploy();
    	undertow.stop();
    	manager = null;
    	undertow = null;
    }
    
    /**
     * 获取servlet上下文
     *
     * @throws Exception
     */
    @Override
    public Object getContext() {
    	return servletContext;
    }
}
