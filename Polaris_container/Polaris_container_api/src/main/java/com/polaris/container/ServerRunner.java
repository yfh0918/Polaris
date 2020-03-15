package com.polaris.container;

import com.polaris.container.banner.PolarisBannerPrinter;
import com.polaris.container.config.ConfigurationSupport;
import com.polaris.container.listener.ServerListener;
import com.polaris.container.listener.ServerListenerSupport;
import com.polaris.core.config.provider.ConfCompositeProvider;

/**
*
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
public abstract class ServerRunner {
	
    /**
    * startServer
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
	public static void run(String[] args, Class<?> configClass, ServerListener... serverListeners) {
		run(args, new Class<?>[]{configClass}, serverListeners);
	}
    public static void run(String[] args, Class<?>[] configClass, ServerListener... serverListeners) {
    	
    	//banna打印
    	PolarisBannerPrinter.print();
    	
    	//各类参数载入
    	ConfCompositeProvider.INSTANCE.init();
    	
		//载入配置类
		ConfigurationSupport.init(args, configClass);		
    	
    	//载入监听器
    	ServerListenerSupport.init(args, serverListeners);
    	
    	//启动
    	ServerFactory.getServer().start();
    }
}
