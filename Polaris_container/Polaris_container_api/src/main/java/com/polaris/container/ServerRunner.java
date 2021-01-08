package com.polaris.container;

import com.polaris.container.banner.Banner;
import com.polaris.container.banner.BannerPrinter;
import com.polaris.container.config.ConfigurationProxy;
import com.polaris.container.listener.ServerListener;
import com.polaris.container.listener.ServerListenerProxy;
import com.polaris.core.component.InitialProxy;
import com.polaris.core.config.ConfClient;

/**
*
* 类名称：ServerRunner
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
    * @throws Exception 
    * @Exception 
    * @since 
    */
	public static void run(String[] args, Class<?> configClass, ServerListener... serverListeners) throws Exception {
		run(args, new Class<?>[]{configClass}, serverListeners);
	}
    public static void run(String[] args, Class<?>[] configClass, ServerListener... serverListeners) throws Exception {
    	
    	//banna打印
    	BannerPrinter.INSTANCE.init(bannerMode);
    	
    	//各类配置中心的文件
    	ConfClient.INSTANCE.init();
    	
        //载入扩展全局init-spi机制
        InitialProxy.INSTANCE.init();

        //载入扩展Spring的configuration-spi机制
		ConfigurationProxy.INSTANCE.init(args, configClass);		
        
    	//载入扩展载入监听器-spi机制
    	ServerListenerProxy.INSTANCE.init(args, serverListeners);
    	
    	//启动Server-spi机制
    	ServerProxy.INSTANCE.start();;
    }
    
    /**
    * 设置banner模式
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
	private static Banner.Mode bannerMode = Banner.Mode.CONSOLE;
    public static void setBannerMode(Banner.Mode inputBannerMode) {
    	bannerMode = inputBannerMode;
    }
}
