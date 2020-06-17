package com.polaris.core.config;

public interface ConfigChangeNotifier {
	
	/**
	* 从配置中心获取到的配置按照策略 进行回调处理
	* @param  listener 配置监听器 
	* @param  config 配置
	* @param  file 文件名称
	* @param  contents 获取的配置内容
	* @return 
	* @Exception 
	* @since 
	*/
	void notify(ConfigChangeListener listener, Config config,String file, String contents);
}
