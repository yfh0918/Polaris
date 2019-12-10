package com.polaris.workflow;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.polaris.http.supports.MainSupport;

/**
 * 入口启动类
 *
 */
@Configuration
@ComponentScan( basePackages={"com.polaris"})
@EnableTransactionManagement(proxyTargetClass=true)
public class WorkFlowApplication
{
    
    public static void main( String[] args ) throws Exception
    {

		//启动WEB
    	MainSupport.startWebServer(args,new Class[]{WorkFlowApplication.class});
    }
}
