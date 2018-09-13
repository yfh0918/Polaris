package com.polaris.container.jetty.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import javax.servlet.ServletContextListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.PropertyUtils;
import com.polaris.container.jetty.listener.ServerHandlerLifeCycle;
import com.polaris.container.jetty.listener.ServerHandlerListerner;
import com.polaris.http.filter.RequestFirstFilterInitializer;

/**
 * Class Name : JettyServer
 * Description : Jetty服务器
 * Creator : yufenghua
 * Modifier : yufenghua
 */

public class JettyServer {
    private static final LogUtil logger = LogUtil.getInstance(JettyServer.class);
    private static final String MAX_THREADS = "300";//和jetty保持一致

    /**
     * 服务器
     */
    private Server server = null;

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
        	String serverPort = ConfClient.get("server.port","");
            InetSocketAddress addr = new InetSocketAddress("0.0.0.0", Integer.parseInt(serverPort));
            server = new Server(addr);
            QueuedThreadPool threadPool = (QueuedThreadPool)server.getThreadPool();
            threadPool.setMaxThreads(Integer.parseInt(ConfClient.get("server.maxThreads",MAX_THREADS)));

            // 设置在JVM退出时关闭Jetty的钩子。
            server.setStopAtShutdown(true);

            //定义context
            WebAppContext context = new WebAppContext();
            context.setDefaultsDescriptor("webdefault.xml");
            context.setContextPath(""); // Application访问路径
            String resourceBase = PropertyUtils.getFilePath("WebContent");
            File resDir = new File(resourceBase);
            context.setResourceBase(resDir.getCanonicalPath());
            
            //Listener
            List<Class <? extends ServletContextListener>> listenerList = RequestFirstFilterInitializer.getListenerList();
            for (Class <? extends ServletContextListener> listerClass : listenerList) {
            	try {
					context.addEventListener(listerClass.newInstance());
				} catch (Exception e) {
					//nothing
				} 
            }
            this.server.setHandler(context); // 将Application注册到服务器
            context.addBean(new ServerHandlerLifeCycle(context.getServletContext()),true);
            this.server.addLifeCycleListener(ServerHandlerListerner.getInstance());//监听handler
        } catch (IOException e) {
            logger.error(e);
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
    public void start() {

        try {

            //如果已经启动就先停掉
            if (this.server != null && (this.server.isStarted() || this.server.isStarting())) {
                this.server.stop();
                this.server = null;
            }

            //没有初始化过，需要重新初始化
            if (this.server == null) {
                init();
            }

            //启动服务
            this.server.start();
            this.server.join();
        } catch (Exception e) {

            //启动出错的话，清空服务
            this.server = null;
            logger.error(e);
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
            logger.error(e);
        } finally {

            //停止了就清空服务
            this.server = null;
        }

    }
}
