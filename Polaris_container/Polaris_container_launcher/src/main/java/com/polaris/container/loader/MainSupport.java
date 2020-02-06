package com.polaris.container.loader;

import com.polaris.container.ServerFactory;
import com.polaris.container.config.ConfigurationSupport;
import com.polaris.container.listener.ServerListener;
import com.polaris.container.listener.ServerListenerSupport;
import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.ServerHandlerClient;

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
	public static void startServer(String[] args, Class<?> configClass, ServerListener... serverListeners) {
		startServer(args, new Class<?>[]{configClass}, serverListeners);
	}
    public static void startServer(String[] args, Class<?>[] configClass, ServerListener... serverListeners) {
    	
    	//各类参数载入
    	ConfClient.init();
    	
		//载入配置类
		ConfigurationSupport.add(args, configClass);		
    	
    	//载入监听器
    	ServerListenerSupport.add(serverListeners, new ServerListener() {//载入自定义的监听

			@Override
			public void started() {
				//注册服务
		    	ServerHandlerClient.register();
				
			}
			
			@Override
			public void stopped() {
				//注销服务
		    	ServerHandlerClient.unRegister();
			}
    		
    	});
    	
    	//开始
    	ServerListenerSupport.starting();
    	
    	//启动
    	ServerFactory.getServer().start();
    }
}
