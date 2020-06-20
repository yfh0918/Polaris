package com.polaris.demo;


import java.io.IOException;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebListener;

import org.springframework.core.annotation.Order;

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
    
    @WebInitParam(name="afdddddddddd",value="ddddddddddddd")
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
    
    @WebFilter(filterName="demofilter",urlPatterns={"/*"}, initParams={
            @WebInitParam(name = "noLoginPaths", value = "index.jsp;fail.jsp;/LoginServlet")
            })
    @Order(2)
    static public class DemoFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            System.out.println("DemoFilter");
            System.out.println(request.getServletContext().getInitParameter("testdemo"));
            chain.doFilter(request, response);
        }

    }

}
