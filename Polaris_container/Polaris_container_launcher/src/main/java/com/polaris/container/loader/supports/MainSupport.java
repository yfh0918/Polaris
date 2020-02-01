package com.polaris.container.loader.supports;

import com.polaris.container.ServerFactory;
import com.polaris.container.listener.ServerListener;
import com.polaris.container.listener.ServerListenerSupport;
import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.NamingClient;

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
    * startServer
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
	public static void startServer(String[] args, ServerListener... serverListener) {
		startServer(args, null, serverListener);
	}
    public static void startServer(String[] args, Class<?> rootConfigClass, ServerListener... serverListeners) {
    	
    	//各类参数载入
    	ConfClient.init(rootConfigClass,args);
    	
    	//载入监听器
    	ServerListenerSupport.add(serverListeners, new ServerListener() {//载入自定义的监听

			@Override
			public void started() {
				//注册服务
		    	NamingClient.register();
				
			}
			
			@Override
			public void stopped() {
				//注销服务
		    	NamingClient.unRegister();
			}
    		
    	});
    	
    	//开始
    	ServerListenerSupport.starting();
    	
    	//启动
    	ServerFactory.getServer().start();
    }
    

    /**
     * stopServer(关闭web容器)
     * @param 
     * @return 
     * @Exception 
     * @since 
     */
     public static void stopServer() {
     	
     	 //关闭
    	 ServerFactory.getServer().stop();
     }
}
