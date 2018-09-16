package com.polaris.config.zk;

public class Constant extends com.polaris.comm.Constant{
	public static final String CONF_DATA_PATH = "/polaris_conf";
	public volatile static String ZK_ADDRESS_CONF;		// zk地址配置中心：格式	ip1:port,ip2:port,ip3:port
	public static final String ZK_NAME_CONF = "config.registry.address";
	public static final String ZK_CONF_MUST = "config.registry.must";
}
