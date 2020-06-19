package com.polaris.demo;


import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.stereotype.Component;

import com.polaris.container.ServerRunner;
import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.listener.ServerListener;
import com.polaris.container.servlet.filter.FlowControlFilter;
import com.polaris.container.servlet.initializer.WebFilterRegister;
import com.polaris.container.servlet.initializer.WebFilterRegister.WebFilterBean;
import com.polaris.container.servlet.initializer.WebInitParamRegister;
import com.polaris.container.servlet.initializer.WebListenerRegister;
import com.polaris.core.component.LifeCycle;

/**
 * 入口启动类
 *
 */
@PolarisApplication
public class DemoApplication
{
    
    public static void main( String[] args ) throws Exception
    {
    	ServerRunner.run(args,DemoApplication.class, new ServerListener() {
            @Override
            public void starting(LifeCycle event) {
                //iniParam
                WebInitParamRegister.register("testdemo", "testdemo");
                
                //listen
                WebListenerRegister.register(MyServletContextListener2.class);
                
                //filter
                WebFilterBean filterBean = new WebFilterBean();
                filterBean.setFilterName("flowcontrol");
                FlowControlFilter filter = new FlowControlFilter();
                Properties properties = new Properties();
                properties.put("server.flowcontrol.permits", "100");
                properties.put("server.flowcontrol.bufferSize", "150");
                properties.put("server.flowcontrol.timeout", "30000");
                filter.load(properties);
                filterBean.setFilter(filter);
                WebFilterRegister.register(filterBean);
            }
        });
    }
    
    static public class MyServletContextListener2 implements ServletContextListener {
        @Override
           public void contextDestroyed(ServletContextEvent sce) {
               System.out.println("===========================MyServletContextListener2销毁");
           }
    
           @Override
           public void contextInitialized(ServletContextEvent sce) {
               System.out.println("===========================MyServletContextListener2初始化");
               System.out.println(sce.getServletContext().getServerInfo());
           }
    
   }
    
    @Component
    @WebListener
    static public class MyServletContextListener implements ServletContextListener {
         @Override
            public void contextDestroyed(ServletContextEvent sce) {
                System.out.println("===========================MyServletContextListener销毁");
            }
     
            @Override
            public void contextInitialized(ServletContextEvent sce) {
                System.out.println("===========================MyServletContextListener初始化");
                System.out.println(sce.getServletContext().getServerInfo());
            }
     
    }

}
