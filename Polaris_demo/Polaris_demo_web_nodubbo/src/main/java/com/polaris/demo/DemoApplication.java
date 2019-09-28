package com.polaris.demo;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.polaris.core.Launcher;
import com.polaris.demo.configurer.WebConfig;
import com.polaris.http.supports.MainSupport;

/**
 * 入口启动类
 *
 */
@Configuration
@ComponentScan( basePackages={"com.polaris"},
 excludeFilters = { @Filter(type=FilterType.ANNOTATION,value=EnableWebMvc.class)}
)
//@ComponentScan( basePackages={"com.polaris"}
//)
public class DemoApplication implements Launcher
{
    
    public static void main( String[] args ) throws Exception
    {
    	
//    	//载入需要的参数，过滤器，监听器等
//    	WebConfigInitializer.loadRootConfig(DemoApplication.class);
//    	WebConfigInitializer.loadWebConfig(WebConfig.class);
//    	WebConfigInitializer.loadInitParameters(key, value);
//    	WebConfigInitializer.loadFilter(filterName, filter, urlPatterns);
//    	WebConfigInitializer.loadListener(listener);
//    	MainSupport.startWebServer(args);//resteasy
    	
		//启动WEB
    	MainSupport.startWebServer(args, new Class[]{DemoApplication.class}, new Class[]{WebConfig.class});//springmvc
    }
}
