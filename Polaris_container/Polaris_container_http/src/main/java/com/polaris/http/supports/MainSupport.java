package com.polaris.http.supports;

import javax.servlet.ServletContext;

import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.NameingClient;
import com.polaris.http.factory.ContainerServerFactory;
import com.polaris.http.listener.ServerListener;

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
	
    /**
    * startWebServer(启动web容器)
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
    public static void startWebServer(String[] args, Class<?> rootConfigClass) {
    	
    	//各类参数载入
    	ConfClient.init(rootConfigClass);
    	
    	//启动
    	ContainerServerFactory.startServer(new ServerListener() {

			@Override
			public void started(ServletContext servletContext) {
				//注册服务
		    	NameingClient.register();
				
			}
			
			@Override
			public void stopped(ServletContext servletContext) {
				//注销服务
		    	NameingClient.unRegister();
			}
    		
    	});
    }
    

    /**
     * stopWebServer(关闭web容器)
     * @param 
     * @return 
     * @Exception 
     * @since 
     */
     public static void stopWebServer() {
     	
     	//关闭
    	 ContainerServerFactory.stopServer();
     }
}
