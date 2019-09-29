package com.polaris.http.supports;

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
public class MainSupport {
	
	/**
	* 创建一个新的实例 MainSupport.
	*
	*/
	private MainSupport() {}

    
    /**
    * startWebServer(启动web容器)
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
    public static void startWebServer(String[] args, Class<?>[] rootConfig, Class<?>[] webConfig) {
    	
    	//各类参数载入
    	ConfClient.init();
    	
    	//启动
    	ContainerServerFactory.startServer(rootConfig, webConfig, new ServerListener() {

			@Override
			public void started() {
				//注册服务
		    	NameingClient.register();
				
			}
			
			@Override
			public void stopped() {
				//注销服务
		    	NameingClient.unRegister();
			}
    		
    	});
    }
    
    public static void startWebServer(String[] args, Class<?>[] rootConfig) {
    	
    	//启动
    	startWebServer(args, rootConfig, null);
    }
    
    public static void startWebServer(String[] args) {
    	
    	//启动
    	startWebServer(args, null, null);
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
