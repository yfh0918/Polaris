package com.polaris.sentinel;

import com.alibaba.csp.sentinel.init.InitFunc;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.StringUtil;

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
public class DataSourceInit implements InitFunc {
	
	 @Override
	 public void init() throws Exception {

		//获取参数 
		if (StringUtil.isEmpty(System.getProperty("csp.sentinel.dashboard.server"))) {
			if (StringUtil.isNotEmpty(ConfClient.get("csp.sentinel.dashboard.server"))) {
				System.setProperty("csp.sentinel.dashboard.server", ConfClient.get("csp.sentinel.dashboard.server"));
			}
		}
		if (StringUtil.isEmpty(System.getProperty("csp.sentinel.api.port"))) {
			if (StringUtil.isNotEmpty(ConfClient.get("csp.sentinel.api.port"))) {
				System.setProperty("csp.sentinel.api.port", ConfClient.get("csp.sentinel.api.port"));
			}
		}
		if (StringUtil.isEmpty(System.getProperty("csp.sentinel.heartbeat.interval.ms"))) {
			if (StringUtil.isNotEmpty(ConfClient.get("csp.sentinel.heartbeat.interval.ms"))) {
				System.setProperty("csp.sentinel.heartbeat.interval.ms", ConfClient.get("csp.sentinel.heartbeat.interval.ms"));
			}
		}
		System.setProperty("project.name", ConfClient.getAppName());
		

		//web过滤
		WebFilterInit webFilterInit = new WebFilterInit();
		webFilterInit.init();
		
		//获取类型参数
		String datasource = System.getProperty("csp.sentinel.datasource");
		if (StringUtil.isEmpty(datasource)) {
			datasource = ConfClient.get("csp.sentinel.datasource");
			if (StringUtil.isEmpty(datasource)) {
				datasource = "nacos";
			}
		}
		
		//判断数据源类型
		if ("nacos".equals(datasource)) {
			NacosDataSourceInit nacosInit = new NacosDataSourceInit();
			nacosInit.init();
		} else if ("file".equals(datasource)) {
			FileDataSourceInit fileInit = new FileDataSourceInit();
			fileInit.init();
		} 

	}
}
