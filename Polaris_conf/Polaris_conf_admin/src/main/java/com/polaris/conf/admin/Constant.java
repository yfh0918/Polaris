package com.polaris.conf.admin;

public class Constant extends com.polaris.comm.Constant{
	public volatile static String ZK_ADDRESS_CONF;		// zk地址配置中心：格式	ip1:port,ip2:port,ip3:port
	public static final String CONF_DATA_PATH = "/polaris_conf";
	
	public static final String CONSTANT_NAME_SPACE = "namespace";
	public static final String CONSTANT_NAME_SPACE_DEF="namespace.dir";
	public static final String CONSTANT_NAME_SPACE_DELETE = "namespace-delete";
}
