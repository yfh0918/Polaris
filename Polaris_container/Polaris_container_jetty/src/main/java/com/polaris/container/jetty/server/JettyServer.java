package com.polaris.container.jetty.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import javax.servlet.ServletContext;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.jetty.listener.ServerHandlerLifeCycle;
import com.polaris.container.jetty.listener.ServerHandlerListerner;
import com.polaris.container.listener.ServerListener;
import com.polaris.container.servlet.listener.WebsocketListerner;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.PropertyUtils;

/**
 * Class Name : JettyServer
 * Description : Jetty服务器
 * Creator : yufenghua
 * Modifier : yufenghua
 */

public class JettyServer {
    private static final Logger logger = LoggerFactory.getLogger(JettyServer.class);
    private static final String MAX_THREADS = "300";//和tomcat保持一致
    private static final  int MAX_SAVE_POST_SIZE = 4 * 1024;
    /**
     * 服务器
     */
    private Server server = null;
    
    private ServerListener startlistener;
    
    /**
     * servlet上下文
     */
    private ServletContext servletContext;

    /**
     * 私有构造方法
     */
    private JettyServer() {
    }

    /**
     * 服务器初始化
     */
    private void init() {
        // 构造服务器
        try {

            //定义server
        	String serverPort = ConfClient.get(Constant.SERVER_PORT_NAME, Constant.SERVER_PORT_DEFAULT_VALUE);
            InetSocketAddress addr = new InetSocketAddress("0.0.0.0", Integer.parseInt(serverPort));
            server = new Server(addr);
            QueuedThreadPool threadPool = (QueuedThreadPool)server.getThreadPool();
            threadPool.setMaxThreads(Integer.parseInt(ConfClient.get("server.maxThreads",MAX_THREADS)));

            // 设置在JVM退出时关闭Jetty的钩子。
            server.setStopAtShutdown(true);

            //定义context
            WebAppContext context = new WebAppContext();
            context.setDefaultsDescriptor("webdefault.xml");
            String contextPath =ConfClient.get(Constant.SERVER_CONTEXT,"/"); 
            if (!contextPath.startsWith("/")) {
            	contextPath = "/" + contextPath;
            }
            context.setContextPath(contextPath); // Application访问路径
            String resourceBase = PropertyUtils.getFullPath("WebContent");
            File resDir = new File(resourceBase);
            context.setResourceBase(resDir.getCanonicalPath());
            context.setMaxFormContentSize(Integer.parseInt(ConfClient.get("server.maxSavePostSize",String.valueOf(MAX_SAVE_POST_SIZE))));
            servletContext = context.getServletContext();
            context.addBean(new ServerHandlerLifeCycle(servletContext),false);
            //context加入server
            this.server.setHandler(context); // 将Application注册到服务器
            this.server.addLifeCycleListener(
            		new ServerHandlerListerner(
            				new WebsocketListerner(),
            				startlistener));//监听handler
        } catch (IOException e) {
            logger.error(e.getMessage());
        } 

    }
    
    /**
     * 获取单实例公共静态方法
     *
     * @return 单实例
     */
    public static JettyServer getInstance() {
        return Singletone.INSTANCE;
    }

    /**
     * 静态内部类实现单例
     */
    private static class Singletone {
        /**
         * 单实例
         */
        private static final JettyServer INSTANCE = new JettyServer();
    }

    /**
     * 启动服务器
     *
     * @throws Exception
     */
    public void start(ServerListener listener) {

        try {

            //如果已经启动就先停掉
            if (this.server != null && (this.server.isStarted() || this.server.isStarting())) {
                this.server.stop();
                this.server = null;
            }

            //没有初始化过，需要重新初始化
            if (this.server == null) {
            	startlistener = listener;
                init();
            }

            //启动服务
            this.server.start();
            
            // add shutdown hook to stop server
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                    	server.stop();
                    } catch (Exception e) {
                        logger.error("failed to stop jetty.", e);
                    }
                }
            });
            
            this.server.join();
        } catch (Exception e) {

            //启动出错的话，清空服务
            this.server = null;
            logger.error("Error:",e);
        }


    }

    /**
     * 停止服务器
     *
     * @throws Exception
     */
    public void stop() {

        try {
            if (this.server != null) {
                if (!this.server.isStopped() && !this.server.isStopping()) {
                    this.server.stop();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {

            //停止了就清空服务
            this.server = null;
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
