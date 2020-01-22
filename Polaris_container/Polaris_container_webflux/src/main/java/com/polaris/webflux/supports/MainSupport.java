package com.polaris.webflux.supports;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.SpringUtil;

import reactor.netty.DisposableServer;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

/**
*
* 项目名称：Polaris_comm
* 类名称：MainSupport
* 类描述：
* 创建人：yufenghua
* 创建时间：2018年5月9日 上午8:55:18
* 修改人：yufenghua
* 修改时间：2018年5月9日 上午8:55:18
* 修改备注：
* @version
*
*/
public abstract class MainSupport {
	private static final Logger logger = LoggerFactory.getLogger(MainSupport.class);
    /**
    * startWebServer(启动web容器)
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
    public static void startWebflux(String[] args, Class<?> rootConfigClass) throws IOException{
    	
    	//各类参数载入
    	ConfClient.init(rootConfigClass);
    	
    	//创建context
    	SpringUtil.refresh();
    	
    	//通过ApplicationContext创建HttpHandler
        HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(SpringUtil.getApplicationContext()).build();
        ReactorHttpHandlerAdapter httpHandlerAdapter = new ReactorHttpHandlerAdapter(httpHandler);
        int port = Integer.parseInt(ConfClient.get(Constant.SERVER_PORT_NAME, Constant.SERVER_PORT_DEFAULT_VALUE));
        DisposableServer server =
                HttpServer.create()
                		  .port(port)
                		  .protocol(new HttpProtocol[] { HttpProtocol.HTTP11 })
                		  .compress(true)
                          .handle(httpHandlerAdapter) 
                          .bindNow();
        logger.info("webflux is started,port:{}",port);

        //block
        server.onDispose().block();
    }
    

    
}
