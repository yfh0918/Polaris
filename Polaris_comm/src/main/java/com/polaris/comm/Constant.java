package com.polaris.comm;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.polaris.comm.thread.InheritablePolarisThreadLocal;

public class Constant {
	
	/**
	 * 返回给前端业务处理状态 0：成功
	 */
	public static final int  STATUS_SUCCESS=0;
	
	/**
	 * 返回给前端业务处理状态 1：失败
	 */
	public static final int STATUS_FAILED=1;
	
	/**
	 * 返回给前端业务处理状态 2：系统错误
	 */
	public static final int STATUS_SYSTEM_FAILED=2;

	/**
	 * 返回给前端业务处理状态 3：没有权限
	 */
	public static final int STATUS_PERMISSION_DENIED=3;

	
	/**
	 * 返回给前端业务处理状态 4：认证失败
	 */
	public static final int STATUS_AUTHENTICATION_FAILED=4;
	
	
	/**
	 * 返回给前端业务处理状态 5：浏览器不支持
	 */
	public static final int STATUS_BROWSER_DENIED=5;

	/**
	 * 返回给前端业务处理状态 7：请求被限制
	 */
    public static final int STATUS_REQUEST_BLOCKED=7;

	/**
	 * 分割符号
	 */
	public static final String SESSION_YH_ID_MC_SPLIT = "-polaris-";

	/**
	 * session保存的token
	 */
	public static final String USER_TOKEN = "token";
	public static final String REFRESH_USER_TOKEN = "refreshToken";
	public static final int DEFAULT_TOKEN_TIMEOUT = 30 * 60;
	public static final int DEFAULT_REFRESH_TOKEN_TIMEOUT = 30 * 24 * 60 * 60;
	
	/**
	 * 全局异常信息
	 */
	public static final String MESSAGE_GLOBAL_ERROR = "系统繁忙，请稍后再试！";

	/**
	 * http连接参数
	 */
	public final static String METHOD = "method";
	public final static String METHOD_GET = "GET";
	public final static String METHOD_POST = "POST";
	public final static String UTF_CODE = "UTF-8";
	public final static int CONNECT_MAX_TIME = 3000000;//连接的最大长度（单位毫秒）
	
	//请求内容
	public static final String REQUEST_BODY = "requestBody";
	
	//构造函数
	private static final InheritablePolarisThreadLocal<Map<String, String>> holder=new InheritablePolarisThreadLocal<Map<String,String>>(){
		@Override protected Map<String,String>initialValue(){
			return new HashMap<String,String>();
		}
	};

	public static void setContext(String key, String value) {
		Map<String, String> map = holder.get();
		map.put(key, value);
		holder.set(map);
	}
	public static String getContext(String key) {
		return holder.get().get(key);
	}
	public static void removeContext(String key) {
		holder.get().remove(key);
	}
	public static Map<String,String> getContext() {
		return holder.get();
	}
	public static void removeContext() {
		holder.remove();
	}
	
	public static final String SLASH = "/";
	public static final String DEFAULT_VALUE = "default";
	public static final long WARCH_TIME = 30000L;

	/**
	 * 参数
     */
	public static String CONFIG = "config";
	public static String DEFAULT_CONFIG_NAME = "application.properties";
	public static String PROJECT_PROPERTY = CONFIG + File.separator + DEFAULT_CONFIG_NAME;
	
	public static String PROJECT_EXTENSION_PROPERTIES = "project.extension.properties";
	
	// 应用名称
	public static String PROJECT_ENV_NAME = "project.env";

	// 应用名称
	public static String PROJECT_NAME = "project.name";
	
	// server端口号和注册中心
	public static String SERVER_PORT_NAME = "server.port";
	public static String NAME_REGISTRY_SWITCH = "name.registry.switch";
	public static final String NAMING_REGISTRY_ADDRESS_NAME = "name.registry.address";

	// dubbo端口号 和注册中心
	public static final String DUBBO_PROTOCOL_PORT_NAME = "dubbo.protocol.port";
	public static final String DUBBO_REGISTRY_ADDRESS_NAME = "dubbo.registry.address";

	// 命名空间(注册中心，配置中心公用)
	public static final String PROJECR_NAMESPACE_NAME="project.namespace";
	
	// 集群名称(注册中心，配置中心公用)
	public static final String PROJECR_CLUSTER_NAME = "project.cluster";
	
	// 分组名称(注册中心,dubbo)
	public static final String PROJECR_GROUP_NAME = "project.group";
	
	// 配置中心
	public static final String CONFIG_REGISTRY_ADDRESS_NAME = "config.registry.address";
	
	// 日志
	public static final String LOG4J = "log4j.properties";
	
	//换行
	public static final String LINE_SEP = System.getProperty("line.separator");
	
	//开关
	public static final String SWITCH_ON = "on";
	public static final String SWITCH_OFF = "off";
	
	//随机数
	public static final String UUID_WORKID = "uuid.wokerId";
	public static final String UUID_DATACENTERID="uuid.datacenterId";

	//外部可以设置路径
	public static void setConfigPath(String path, String fileName) {
		DEFAULT_CONFIG_NAME = fileName;
		CONFIG = path;
		PROJECT_PROPERTY = CONFIG + File.separator + DEFAULT_CONFIG_NAME;
	}
}
