package com.polaris.container.undertow.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.server.HttpHandler;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

/**
 * Class Name : UndertowServer
 * Description : Undertow服务器
 * Creator : yufenghua
 * Modifier : yufenghua
 */

public class UndertowServer {
    private static final Logger logger = LoggerFactory.getLogger(UndertowServer.class);
//    private static final String MAX_THREADS = "300";//和tomcat保持一致
//    private static final  int MAX_SAVE_POST_SIZE = 4 * 1024;
    
    private Undertow undertow;
    private DeploymentManager manager;
    
    /**
     * servlet上下文
     */
    private ServletContext servletContext;

    /**
     * 私有构造方法
     */
    private UndertowServer() {
    }

    /**
     * 服务器初始化
     * @throws ServletException 
     */
    private void init() throws ServletException {
    	DeploymentManager manager = createDeploymentManager();
    	int port = 8080;
    	Builder builder = createBuilder(port);
    	HttpHandler httpHandler = manager.start();
		builder.setHandler(httpHandler);
		undertow =  builder.build();
    }
    private Builder createBuilder(int port) {
		Builder builder = Undertow.builder();
//		if (this.bufferSize != null) {
//			builder.setBufferSize(this.bufferSize);
//		}
//		if (this.ioThreads != null) {
//			builder.setIoThreads(this.ioThreads);
//		}
//		if (this.workerThreads != null) {
//			builder.setWorkerThreads(this.workerThreads);
//		}
//		if (this.directBuffers != null) {
//			builder.setDirectBuffers(this.directBuffers);
//		}
//		if (getSsl() != null && getSsl().isEnabled()) {
//			customizeSsl(builder);
//		}
//		else {
			builder.addHttpListener(port, "0.0.0.0");
//		}
//		for (UndertowBuilderCustomizer customizer : this.builderCustomizers) {
//			customizer.customize(builder);
//		}
		return builder;
	}

    private DeploymentManager createDeploymentManager() {
		DeploymentInfo deployment = Servlets.deployment();
//		deployment.setClassLoader(getServletClassLoader());
//		deployment.setContextPath(getContextPath());
//		deployment.setDisplayName(getDisplayName());
//		deployment.setDeploymentName("xxx");
//		deployment.setServletStackTraces(ServletStackTraces.NONE);
//		deployment.setResourceManager(getDocumentRootResourceManager());
//		deployment.setTempDir(createTempDir("undertow"));
//		deployment.setEagerFilterInit(this.eagerInitFilters);
		manager = Servlets.newContainer().addDeployment(deployment);
		manager.deploy();
    	servletContext = manager.getDeployment().getServletContext();
		SessionManager sessionManager = manager.getDeployment().getSessionManager();
		sessionManager.setDefaultSessionTimeout(30 * 60);//30mins
		return manager;
	}
    
    /**
     * 获取单实例公共静态方法
     *
     * @return 单实例
     */
    public static UndertowServer getInstance() {
        return Singletone.INSTANCE;
    }

    /**
     * 静态内部类实现单例
     */
    private static class Singletone {
        /**
         * 单实例
         */
        private static final UndertowServer INSTANCE = new UndertowServer();
    }

    /**
     * 启动服务器
     *
     * @throws Exception
     */
    public void start() {
    	try {

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

            // add shutdown hook to stop server
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                    	undertow.stop();
                    	manager.stop();
                    	manager.undeploy();
                    } catch (Exception e) {
                        logger.error("failed to stop undertow.", e);
                    }
                }
            });
        } catch (Exception e) {

            //启动出错的话，清空服务
            this.undertow = null;
            logger.error(e.getMessage());
        }
    }

    
    /**
     * 获取servlet上下文
     *
     * @throws Exception
     */
    public ServletContext getServletContex() {
    	return servletContext;
    }
}
