package com.polaris.container.jetty.server;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.ServiceLoader;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.SpringContextServer;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.FileUtil;

/**
 * Class Name : JettyServer
 * Description : Jetty服务器
 * Creator : yufenghua
 * Modifier : yufenghua
 */

public class JettyServer extends SpringContextServer{
    private static final Logger logger = LoggerFactory.getLogger(JettyServer.class);
    private static final String MAX_THREADS = "300";//和tomcat保持一致
    private static final  int MAX_SAVE_POST_SIZE = 4 * 1024;
    /**
     * 服务器
     */
    private Server server = null;
    
    /**
     * servlet上下文
     */
    private ServletContext servletContext;
    
    private String serverPort;
    private String contextPath;

    /**
     * 私有构造方法
     */
    private JettyServer() {
    }

    /**
     * 服务器初始化
     */
    private void init() throws Exception{
        //定义server
    	serverPort = ConfClient.get(Constant.SERVER_PORT_NAME, Constant.SERVER_PORT_DEFAULT_VALUE);
        InetSocketAddress addr = new InetSocketAddress("0.0.0.0", Integer.parseInt(serverPort));
        server = new Server(addr);
        QueuedThreadPool threadPool = (QueuedThreadPool)server.getThreadPool();
        threadPool.setMaxThreads(Integer.parseInt(ConfClient.get("server.maxThreads",MAX_THREADS)));

        // 设置在JVM退出时关闭Jetty的钩子。
        server.setStopAtShutdown(true);

        //定义context
        WebAppContext context = new WebAppContext();
        context.setDefaultsDescriptor("webdefault.xml");
        contextPath =ConfClient.get(Constant.SERVER_CONTEXT,"/"); 
        if (!contextPath.startsWith("/")) {
        	contextPath = "/" + contextPath;
        }
        context.setContextPath(contextPath); // Application访问路径
        String resourceBase = FileUtil.getFullPath("WebContent");
        File resDir = new File(resourceBase);
        context.setResourceBase(resDir.getCanonicalPath());
        context.setMaxFormContentSize(Integer.parseInt(ConfClient.get("server.maxSavePostSize",String.valueOf(MAX_SAVE_POST_SIZE))));
        servletContext = context.getServletContext();
        context.addBean(new JettyServletContainerInitializer(servletContext),false);
        //context加入server
        this.server.setHandler(context); // 将Application注册到服务器
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
    @Override
    public void start() throws Exception{

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
        
        //启动日志
        logger.info("Jetty started on port(s) " + this.serverPort + " with context path '" + this.contextPath + "'");
    }
    
    /**
     * 停止服务服务器
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        super.stop();
    	server.stop();
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
    
    public static class JettyServletContainerInitializer extends AbstractServletContainerInitializerCaller {
    	ServletContext sc;
    	private final ServiceLoader<ServletContainerInitializer> serviceLoader = ServiceLoader.load(ServletContainerInitializer.class);
    	
    	public JettyServletContainerInitializer(ServletContext sc) {
    		this.sc = sc;
    	}
    	@Override
    	public void start() throws Exception {
    		for (ServletContainerInitializer servletContainerInitializer : serviceLoader) {
    			try {
    				ContextHandler.getCurrentContext().setExtendedListenerTypes(true);
    				servletContainerInitializer.onStartup(new HashSet<>(), sc);
    			} catch (Exception e) {
    				e.printStackTrace();
    			} finally {
    				ContextHandler.getCurrentContext().setExtendedListenerTypes(false);
    			}
    		}
    	}
    }
}
