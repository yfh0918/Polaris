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
	public static String PROJECT_CONFIG_NAME = "project.config.name";
	public static String SPRING_CONFIG_LOCACTION = "spring.config.location";	
	
	// appName关联
    public static final String PARAM_MARKING_PROJECT = "project.name";
    public static final String PARAM_MARKING_SPRINGBOOT = "spring.application.name";
    public static final String SUN_JAVA_COMMAND = "sun.java.command";
    public static final String JAR_SUFFIX_LOWER = ".jar";
    public static final String JAR_SUFFIX_UPPER = ".JAR";
    
	// serverContext
	public static String SERVER_CONTEXT = "server.contextPath";
	public static String SERVER_SPRING_CONTEXT = "server.servlet.context-path";

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
	public static final String LOG_TRACE_ENABEL="logging.trace.enable";
	public static final String LOG_CONFIG_FILE_KEY = "log4j.configurationFile";
	public static final String LOG_CONFIG_KEY="logging.config";
	public static final String DEFAULT_LOG_FILE = "classpath:config/log4j2.xml";
	
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
	
	//classpath
	public static final String CLASS_PATH = "classpath:";
	
	//system调用
	public static final String SYSTEM_CALL_HEADER_KEY = "system.call.token.key";
	public static final String SYSTEM_CALL_HEADER_KEY_DEFAULT = "X-System-Call-TokenID";
	public static final String SYSTEM_CALL_START_WITH = "system.call.start";
	public static final String SYSTEM_CALL_START_WITH_DEFAULT = "X18KgcXJMRq8NO7ikrVfXNGMSKso=zUYRzNFdz2Z";
	public static final String SYSTEM_CALL_ENCRYPT_KEY = "system.call.encrypt.key";
	public static final String SYSTEM_CALL_ENCRYPT_KEY_DEFAULT = "polaris-sys@cl01";
	public static final String SYSTEM_CALL_ENCRYPT_VALUE = "system.call.encrypt.value";
	public static final String SYSTEM_CALL_ENCRYPT_VALUE_DEFAULT = "polaris-system-call-001";
	
	public static final String NEW_LINE="\n";
	public static final String RETURN ="\r";
	public static final String EMPTY = "";
	
}
