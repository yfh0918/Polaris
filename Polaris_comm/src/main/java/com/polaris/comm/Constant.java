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
	 * session保存的登陆ID
	 */
	public static final String SESSION_USER_ID = "session_yh_mc";
	
	/**
	 * session保存的YH_ID
	 */
	public static final String SESSION_YH_ID = "session_yh_id";
	
	/**
	 * session保存的YH_ID和用户名称的分割符号
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
	 * 请求区分（0：浏览器登陆用户，1：token用户例如定时器）
	 */
	public static final String REQUEST_TYPE = "request_type";
	public static final String LOGIN_USER_TYPE = "0";//登陆用户
	public static final String TOKEN_USER_TYPE = "1";//内部用户（外部授权的token，不需要指定request_type）
	
	/**
	 * 查询成功
	 */
	public static final String MESSAGE_SEARCH_SUCCESS = "查询成功！";
	
	/**
	 * 查询失败
	 */
	public static final String MESSAGE_SEARCH_ERROR = "查询失败！";
	
	/**
	 * 查询结果为空
	 */
	public static final String MESSAGE_SEARCH_ZERO = "没有数据！";

	/**
	 * 操作成功
	 */
	public static final String MESSAGE_OPERATE_SUCCESS = "操作成功！";

	/**
	 * 操作失败
	 */
	public static final String MESSAGE_OPERATE_ERROR = "操作失败！";

	/**
	 * 修改成功
	 */
	public static final String MESSAGE_UPDATE_SUCCESS = "修改成功！";

	/**
	 * 修改失败
	 */
	public static final String MESSAGE_UPDATE_ERROR = "修改失败！";
	
	/**
	 * 新增成功
	 */
	public static final String MESSAGE_INSERT_SUCCESS = "新增成功！";

	/**
	 * 新增失败
	 */
	public static final String MESSAGE_INSERT_ERROR = "新增失败！";

	/**
	 * 删除成功
	 */
	public static final String MESSAGE_DELETE_SUCCESS = "删除成功！";

	/**
	 * 删除失败
	 */
	public static final String MESSAGE_DELETE_ERROR = "删除失败！";

	public static final String MESSAGE_DELETE_NOT_ALLOWED = "不能删除的数据！";

	/**
	 * 数据为选择
	 */
	public static final String MESSAGE_DATA_NO_SELECTED = "未选择数据！";
	
	/**
	 * 数据库异常
	 */
	public static final String MESSAGE_DB_ERROR = "数据库异常，请联系管理员！";

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
	public static String PROJECT_PROPERTY = CONFIG + File.separator + "application.properties";
	public static String PROJECT_EXTENSION_PROPERTIES = "project.extension.properties";
	
	// 应用名称
	public static String PROJECT_ENV_NAME = "project.env";

	// 应用名称
	public static String PROJECT_NAME = "project.name";
	
	// server端口号和注册中心
	public static String SERVER_PORT_NAME = "server.port";
	public static final String NAMING_REGISTRY_ADDRESS_NAME = "name.registry.address";

	// dubbo端口号 和注册中心
	public static final String DUBBO_PROTOCOL_PORT_NAME = "dubbo.protocol.port";
	public static final String DUBBO_REGISTRY_ADDRESS_NAME = "dubbo.registry.address";


	// 命名空间
	public static final String PROJECR_NAMESPACE_NAME="project.namespace";
	
	// 集群名称
	public static final String PROJECR_CLUSTER_NAME = "project.cluster";
	
	// 配置中心
	public static final String CONFIG_REGISTRY_ADDRESS_NAME = "config.registry.address";
	
	// 日志
	public static final String LOG4J = "log4j.properties";

}
