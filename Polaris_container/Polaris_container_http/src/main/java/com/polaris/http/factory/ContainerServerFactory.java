package com.polaris.http.factory;

import com.polaris.comm.util.LogUtil;

public class ContainerServerFactory {
    private static final LogUtil logger = LogUtil.getInstance(ContainerServerFactory.class);

    private ContainerServerFactory() {
    }

    public static void newInstance() {

        //默认为jetty
        try {
            Class.forName("com.polaris.container.jetty.main.Main").newInstance();
        } catch (Exception ex) {
            logger.info("tomcat启动！");
            try {
                Class.forName("com.polaris.container.tomcat.main.Main").newInstance();
            } catch (Exception ex2) {
                logger.error("Server启动失败，请查看是否配置了Jetty或者Tomcat!", ex2);
            }
        }
    }
}
