package com.polaris.dubbo.supports;

import com.polaris.core.config.ConfClient;

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
    * startDubboServer(启动dubbo容器)
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
    public static void startDubboServer(String[] args) {
    	//载入参数
    	ConfClient.init();
		//启动
		org.apache.dubbo.container.Main.main(args);
    }
    
}
