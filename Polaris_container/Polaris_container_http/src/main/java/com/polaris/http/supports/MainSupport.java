package com.polaris.http.supports;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.connect.ServerDiscoveryHandlerProvider;
import com.polaris.http.factory.ContainerServerFactory;
import com.polaris.http.util.NetUtils;

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
public class MainSupport extends com.polaris.core.supports.MainSupport{
	
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
    public static void startWebServer(String[] args) {
    	
    	//各类参数载入
    	ConfClient.init();
    	
		//注册服务
		if (Constant.SWITCH_ON.equals(ConfClient.get(Constant.NAME_REGISTRY_SWITCH, Constant.SWITCH_ON))) {
			String registerIp = ConfClient.get(Constant.IP_ADDRESS, NetUtils.getLocalHost());
			ServerDiscoveryHandlerProvider.getInstance().register(registerIp, Integer.parseInt(ConfClient.get(Constant.SERVER_PORT_NAME)));
			
			// add shutdown hook to stop server
	        Runtime.getRuntime().addShutdownHook(new Thread() {
	            public void run() {
	            	ServerDiscoveryHandlerProvider.getInstance().deregister(registerIp, Integer.parseInt(ConfClient.get(Constant.SERVER_PORT_NAME)));
	            }
	        });
		}
		
    	//启动
    	ContainerServerFactory.startServer();
    }
}
