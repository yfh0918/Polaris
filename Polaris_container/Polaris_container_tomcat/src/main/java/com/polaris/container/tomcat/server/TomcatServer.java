package com.polaris.container.tomcat.server;

import java.io.File;

import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.SpringContextServer;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.FileUtil;

/**
 * Class Name : TomcatServer
 * Description : Tomcat服务器
 * Creator : yufenghua
 * Modifier : yufenghua
 */

public class TomcatServer extends SpringContextServer {
    private static final Logger logger = LoggerFactory.getLogger(TomcatServer.class);
    private static final String MAX_THREADS = "300";//和jetty保持一致
    private static final  int MAX_SAVE_POST_SIZE = 4 * 1024;
    private static final  int MAX_HTTP_HEADER_SIZE = 8 * 1024;
    

    /**
     * 服务器
     */
    private Tomcat tomcat = null;

    /**
     * 私有构造方法
     */
    private TomcatServer() {
    }
    
    /**
     * servlet上下文
     */
    private StandardContext standardContext;
    
    private String serverPort;
    private String contextPath;

    /**
     * 服务器初始化
     */
    private void init() throws Exception {

        tomcat = new Tomcat();

        //端口号
        serverPort = ConfClient.get(Constant.SERVER_PORT_NAME, Constant.SERVER_PORT_DEFAULT_VALUE);

        //工作路径
        String resourceBase = FileUtil.getFullPath("WebContent");
        File resDir = new File(resourceBase);
        if (!resDir.exists()) {
            resDir.mkdirs();
        }
        String catalina_home = resDir.getCanonicalPath();
        contextPath =ConfClient.get(Constant.SERVER_CONTEXT,"/"); 
        if (!contextPath.startsWith("/")) {
            contextPath = "/" + contextPath;
        }
        String docBase = "";

        //设置工作目录
        tomcat.setHostname("0.0.0.0");
        tomcat.setPort(Integer.parseInt(serverPort));

        //设置工作目录,其实没什么用,tomcat需要使用这个目录进行写一些东西
        tomcat.setBaseDir(catalina_home);

        //设置程序的目录信息和线程数
        tomcat.getHost().setAppBase(catalina_home);
        Http11NioProtocol protocol = (Http11NioProtocol)tomcat.getConnector().getProtocolHandler();
        protocol.setMaxThreads(Integer.parseInt(ConfClient.get("server.maxThreads",MAX_THREADS)));//设置最大线程数
        protocol.setMaxSavePostSize(Integer.parseInt(ConfClient.get("server.maxSavePostSize",String.valueOf(MAX_SAVE_POST_SIZE))));
        protocol.setMaxHttpHeaderSize(Integer.parseInt(ConfClient.get("server.maxHttpHeaderSize",String.valueOf(MAX_HTTP_HEADER_SIZE))));
        protocol.setConnectionTimeout(Integer.parseInt(ConfClient.get("server.connectionTimeout",String.valueOf(60000))));

        // Add AprLifecycleListener
        StandardServer server = (StandardServer) tomcat.getServer();
        AprLifecycleListener listener = new AprLifecycleListener();
        server.addLifecycleListener(listener);
        
        //加载上下文
        standardContext = new StandardContext();
        
        //其他参数加载
        standardContext.setPath(contextPath);//contextPath
        standardContext.setDocBase(docBase);//文件目录位置
        standardContext.addLifecycleListener(new Tomcat.DefaultWebXmlListener());
        standardContext.addLifecycleListener(new ContextConfig());
     
        //关闭jarScan
        StandardJarScanner jarScanner = new StandardJarScanner();
        jarScanner.setScanClassPath(false);
        jarScanner.setScanManifest(false);
        standardContext.setJarScanner(jarScanner);

        //保证已经配置好了。
        standardContext.addLifecycleListener(new Tomcat.FixContextListener());
        standardContext.setSessionCookieName("jsessionid");
        tomcat.getHost().addChild(standardContext);

    }

    /**
     * 获取单实例公共静态方法
     *
     * @return 单实例
     */
    public static TomcatServer getInstance() {
        return Singletone.INSTANCE;
    }

    /**
     * 静态内部类实现单例
     */
    private static class Singletone {
        /**
         * 单实例
         */
        private static final TomcatServer INSTANCE = new TomcatServer();
    }

    /**
     * 启动服务器
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception{

        //如果已经启动就先停掉
        if (this.tomcat != null) {
            this.tomcat.stop();
            this.tomcat = null;
        }

        //没有初始化过，需要重新初始化
        if (this.tomcat == null) {
            init();
        }

        //启动服务
        this.tomcat.start();
        
        //启动日志
        logger.info("tomcat started on port(s) " + this.serverPort + " with context path '" + this.contextPath + "'");
    }
    
    
    
    /**
     * 停止服务服务器
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        tomcat.stop();
    }
    
    /**
     * 获取servlet上下文
     *
     * @throws Exception
     */
    @Override
    public Object getContext() {
        return standardContext.getServletContext();
    }
}
