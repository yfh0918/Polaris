package com.polaris.container;

import com.polaris.container.listener.ServerListener;

/**
*
* 类名称：MainSupport
* 类描述：
* 创建人：yufenghua
* 创建时间：2018年5月9日 上午8:55:18
* 修改人：yufenghua
* 修改时间：2018年5月9日 上午8:55:18
* 修改备注：
* @see ServerRunner
* @version
*
*/
@Deprecated
public abstract class MainSupport {
	
    /**
    * startServer
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
	public static void startServer(String[] args, Class<?> configClass, ServerListener... serverListeners) {
		ServerRunner.run(args, new Class<?>[]{configClass}, serverListeners);
	}
}
