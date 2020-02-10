package com.polaris.demo;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.listener.ServerListener;
import com.polaris.container.loader.MainSupport;
import com.polaris.demo.notify.WebMVCNotify;

/**
 * 入口启动类
 *
 */
@PolarisApplication
public class DemoApplication
{
    
    public static void main( String[] args ) throws Exception
    {
    	MainSupport.startServer(args,DemoApplication.class, new ServerListener() {
    		@Override
    		public void starting() {
    			
    			//几种方式导入springMVC的Configurer
    			//方式1：事件监听方式，参考本例
    			//方式2：@Configuration
    			//     public class xxxx implements WebMvcConfigurer
    			new WebMVCNotify();
    		}
    	});
    }
}
