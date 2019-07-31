package com.polaris.core.supports;

import com.polaris.core.Constant;

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
abstract public class MainSupport {
	
    /**
    * 设置初始载入路径
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
	public static void setConfigPath(String path, String fileName) {
		Constant.setConfigPath(path, fileName);
	}
	

}
