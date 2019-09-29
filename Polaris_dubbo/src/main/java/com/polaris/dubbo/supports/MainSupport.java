package com.polaris.dubbo.supports;

import java.io.IOException;

import com.polaris.core.config.ConfClient;
import com.polaris.core.util.SpringUtil;

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
     * @throws IOException 
    * @Exception 
    * @since 
    */
    public static void startDubboServer(String[] args, Class<?>... clazzs) throws IOException  {
    	//载入参数
    	ConfClient.init();
    	
    	//载入spring
    	SpringUtil.start(clazzs);
    	
    	//hold
    	System.in.read();
    }
    
}
