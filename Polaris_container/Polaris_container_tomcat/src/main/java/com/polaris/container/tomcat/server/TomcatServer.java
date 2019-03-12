package com.polaris.container.tomcat.server;

import java.io.File;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.scan.StandardJarScanner;

import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.PropertyUtils;

/**
 * Class Name : TomcatServer
 * Description : Tomcat服务器
 * Creator : yufenghua
 * Modifier : yufenghua
 */

public class TomcatServer {
    private static final LogUtil logger = LogUtil.getInstance(TomcatServer.class);
    private static final String MAX_THREADS = "300";//和jetty保持一致

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
     * 服务器初始化
     */
    private void init() {

        // 构造服务器
        try {

            tomcat = new Tomcat();

            //端口号
            String serverPort = ConfClient.get("server.port","");

            //工作路径
            String resourceBase = PropertyUtils.getFilePath("WebContent");
            File resDir = new File(resourceBase);
            if (!resDir.exists()) {
            	resDir.mkdirs();
            }
            String catalina_home = resDir.getCanonicalPath();
            String contextPath = ConfClient.get("server.contextPath","");
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

            // Add AprLifecycleListener
            StandardServer server = (StandardServer) tomcat.getServer();
            AprLifecycleListener listener = new AprLifecycleListener();
            server.addLifecycleListener(listener);

            //加载上下文
            StandardContext standardContext = new StandardContext();
            
            //其他参数加载
            standardContext.setPath(contextPath);//contextPath
            standardContext.setDocBase(docBase);//文件目录位置
            standardContext.addLifecycleListener(new Tomcat.DefaultWebXmlListener());
            standardContext.addLifecycleListener(new ContextConfig());

            //关闭jarScan
            StandardJarScanner jarScanner = new StandardJarScanner();
            jarScanner.setScanManifest(false);
            standardContext.setJarScanner(jarScanner);

            //保证已经配置好了。
            standardContext.addLifecycleListener(new Tomcat.FixContextListener());
            standardContext.setSessionCookieName("jsessionid");
            tomcat.getHost().addChild(standardContext);

        } catch (Exception e) {
            logger.error(e);
        }

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
    public void start() {

        try {

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

            // add shutdown hook to stop server
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        tomcat.stop();
                    } catch (LifecycleException e) {
                        logger.error("failed to stop tomcat.", e);
                    }
                }
            });
            this.tomcat.getServer().await();
        } catch (Exception e) {

            //启动出错的话，清空服务
            this.tomcat = null;
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
            if (this.tomcat != null) {
                this.tomcat.stop();
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {

            //停止了就清空服务
            this.tomcat = null;
        }
    }
}
