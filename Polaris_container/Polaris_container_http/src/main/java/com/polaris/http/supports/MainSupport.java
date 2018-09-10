package com.polaris.http.supports;

import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.StringUtil;
import com.polaris.http.Constant;
import com.polaris.http.factory.ContainerServerFactory;

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
public class MainSupport extends com.polaris.comm.supports.MainSupport{
	
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
    	
    	//log4j重新设定地址
		configureAndWatch(WARCH_TIME);
		
		//端口号
		String port = System.getProperty(Constant.SERVER_PORT);
		if (StringUtil.isEmpty(port)) {
			port = ConfClient.get(Constant.SERVER_PORT);
		}
		
    	//不允许重复启动
    	if (!makeSingle(Constant.SERVER_PORT, port)) {
    		
        	//启动
        	ContainerServerFactory.newInstance();
    	} 
    }
}
