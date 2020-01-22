package com.polaris.core;

public class Constant {
	
    /**
     * 成功
     **/
    public static final String RESULT_SUCCESS = "GL_S000";

    /**
     * 失败
     **/
    public static final String RESULT_FAIL = "GL_F000";
    
    /**
     * token_ID
     **/
    public static final String TOKEN_ID = "X-TokenID";
    
    /**
     * token_error
     **/
    public static final String TOKEN_FAIL_CODE = "GL_F999";
    
	/**
	 * 全局异常信息
	 */
	public static final String MESSAGE_GLOBAL_ERROR = "系统繁忙，请稍后再试！";


	/**
	 * 分割符号
	 */
	public static final String SESSION_YH_ID_MC_SPLIT = "-polaris-";

	
	/**
	 * http连接参数
	 */
	public final static String METHOD = "method";
	public final static String METHOD_GET = "GET";
	public final static String METHOD_POST = "POST";
	public final static String FILE_ENCODING = "file.encoding";
	public final static String UTF_CODE = "UTF-8";
	public final static int CONNECT_MAX_TIME = 3000000;//连接的最大长度（单位毫秒）
	
	//请求内容
	public static final String REQUEST_BODY = "requestBody";
	
	public static final String SLASH = "/";
	public static final String DEFAULT_VALUE = "default";
	public static final long WARCH_TIME = 30000L;

	/**
	 * 参数
     */
	public static String CONFIG = "config";
	public static String PROJECT_CONFIG_NAME = "project.config.name";
	public static String SPRING_CONFIG_LOCACTION = "spring.config.location";
	public static volatile String DEFAULT_CONFIG_NAME = "application.properties";
	
	public static String PROJECT_EXTENSION_PROPERTIES = "project.extension.properties";
	public static String PROJECT_GLOBAL_PROPERTIES = "project.global.properties";
	
	// 应用名称
	public static String PROJECT_NAME = "project.name";
	public static String SPRING_BOOT_NAME = "spring.application.name";//融合springboot
	
	// server端口号和注册中心
	public static String SERVER_PORT_DEFAULT_VALUE = "80";
	public static String SERVER_PORT_NAME = "server.port";
	public static String NAME_REGISTRY_SWITCH = "name.registry.switch";
	public static final String NAMING_REGISTRY_ADDRESS_NAME = "name.registry.address";

	// dubbo端口号 和注册中心
	public static final String DUBBO_PROTOCOL_PORT_NAME = "dubbo.protocol.port";
	public static final String DUBBO_REGISTRY_ADDRESS_NAME = "dubbo.registry.address";

	// 命名空间(注册中心，配置中心公用)
	public static final String PROJECR_NAMESPACE_NAME="project.namespace";
	
	// 集群名称(注册中心，配置中心公用)
	public static final String PROJECR_GROUP_NAME = "project.group";

	// 配置中心
	public static final String CONFIG_REGISTRY_ADDRESS_NAME = "config.registry.address";
	
	// 日志
	public static final String LOG_CONFIG="logging.config";
	public static final String DEFAULT_LOG_FILE = "log4j2.xml";
	
	//换行
	public static final String LINE_SEP = System.getProperty("line.separator");
	
	//注册中心，本机IP
	public static final String IP_ADDRESS = "project.ip";
	
	//开关
	public static final String SWITCH_ON = "on";
	public static final String SWITCH_OFF = "off";
	
	//随机数
	public static final String UUID_WORKID = "uuid.wokerId";
	public static final String UUID_DATACENTERID="uuid.datacenterId";
	
	public static final String CLASS_PATH = "classpath:";
	
}
