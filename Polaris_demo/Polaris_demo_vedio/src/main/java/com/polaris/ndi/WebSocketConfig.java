package com.polaris.ndi;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.WebAppRootListener;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class WebSocketConfig implements ServletContextInitializer{

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(WebAppRootListener.class);
        servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize","6553600");
        servletContext.setInitParameter("org.apache.tomcat.websocket.binaryBufferSize","6553600");
    }
}
