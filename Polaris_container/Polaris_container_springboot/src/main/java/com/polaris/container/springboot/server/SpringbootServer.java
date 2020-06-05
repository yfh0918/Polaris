package com.polaris.container.springboot.server;

import javax.servlet.ServletContext;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.WebApplicationContext;

import com.polaris.container.ServerOrder;
import com.polaris.container.SpringContextServer;
import com.polaris.container.config.ConfigurationHelper;
import com.polaris.container.servlet.ServletContextHelp;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.SpringUtil;

@Order(ServerOrder.SPRINGBOOT)
public class SpringbootServer extends SpringContextServer{
    
    /**
     * 启动服务器
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception{
        
        //projectName is must require
        String projectName = ConfClient.get(Constant.SPRING_BOOT_NAME, ConfClient.get(Constant.PROJECT_NAME));
        ConfClient.set(Constant.SPRING_BOOT_NAME, projectName);
        System.setProperty(Constant.SPRING_BOOT_NAME, projectName);
        
        //context
        String serverContext = ConfClient.get(Constant.SERVER_SPRING_CONTEXT,ConfClient.get(Constant.SERVER_CONTEXT));
        ConfClient.set(Constant.SERVER_SPRING_CONTEXT, serverContext);
        System.setProperty(Constant.SERVER_SPRING_CONTEXT, serverContext);
        
        //start server
        ConfigurationHelper.addConfiguration(SpringbootConfiguration.class);
        SpringApplication springApplication = new SpringApplication(ConfigurationHelper.getConfiguration());
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.setRegisterShutdownHook(false);//统一由容器管理shutdown处理
        ConfigurableApplicationContext context = springApplication.run(ConfigurationHelper.getArgs());
        if (context instanceof WebApplicationContext) {
            ServletContext servletContext = ((WebApplicationContext)context).getServletContext();
            ServletContextHelp.loadServletContext(context, servletContext, false);
        } else {
            SpringUtil.setApplicationContext(context);
        }
    }
}
