package com.polaris.container.springboot.server;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.container.SpringContextServer;
import com.polaris.container.config.ConfigurationHelper;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.SpringUtil;

public class SpringbootServer extends SpringContextServer{
	
	/**
     * 私有构造方法
     */
    private SpringbootServer() {
    }
    
    /**
     * 获取单实例公共静态方法
     *
     * @return 单实例
     */
    public static SpringbootServer getInstance() {
        return Singletone.INSTANCE;
    }

    /**
     * 静态内部类实现单例
     */
    private static class Singletone {
        /**
         * 单实例
         */
        private static final SpringbootServer INSTANCE = new SpringbootServer();
    }
    
    /**
     * 启动服务器
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception{
    	
    	//project
    	String projectName = ConfClient.get(Constant.SPRING_BOOT_NAME, ConfClient.get(Constant.PROJECT_NAME));
    	ConfClient.set(Constant.SPRING_BOOT_NAME, projectName);
    	System.setProperty(Constant.SPRING_BOOT_NAME, projectName);
    	
    	//context
    	String serverContext = ConfClient.get(Constant.SERVER_SPRING_CONTEXT,ConfClient.get(Constant.SERVER_CONTEXT));
    	ConfClient.set(Constant.SERVER_SPRING_CONTEXT, serverContext);
    	System.setProperty(Constant.SERVER_SPRING_CONTEXT, serverContext);
    	
    	//启动应用
    	ConfigurationHelper.addConfiguration(SpringbootConfiguration.class);
    	SpringApplication springApplication = new SpringApplication(ConfigurationHelper.getConfiguration());
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.setRegisterShutdownHook(false);//统一由容器管理shutdown处理
        ConfigurableApplicationContext context = springApplication.run(ConfigurationHelper.getArgs());
        SpringUtil.setApplicationContext(context);
    }
}
