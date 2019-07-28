package com.polaris.core.supports;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfigHandlerProvider;
import com.polaris.core.util.PropertyUtils;
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
	
    /**
    * iniParameter(初期的参数配置)
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
	@SuppressWarnings("static-access")
	public static void iniParameter() {
		
		//配置中心
		String config = System.getProperty(Constant.CONFIG_REGISTRY_ADDRESS_NAME);
		if (StringUtil.isNotEmpty(config)) {
			ConfigHandlerProvider.getInstance().updateCache(Constant.CONFIG_REGISTRY_ADDRESS_NAME, config, Constant.DEFAULT_CONFIG_NAME);
		} 
		
		//环境（production, pre,dev,pre etc）
		String env = System.getProperty(Constant.PROJECT_ENV_NAME);
		if (StringUtil.isNotEmpty(env)) {
			ConfigHandlerProvider.getInstance().updateCache(Constant.PROJECT_ENV_NAME, env, Constant.DEFAULT_CONFIG_NAME);
		} 
		
		//工程名称
		String project = System.getProperty(Constant.PROJECT_NAME);
		if (StringUtil.isNotEmpty(project)) {
			ConfigHandlerProvider.getInstance().updateCache(Constant.PROJECT_NAME, project, Constant.DEFAULT_CONFIG_NAME);
		}
		
		//命名空间(注册中心和配置中心)
		String namespace = System.getProperty(Constant.PROJECR_NAMESPACE_NAME);
		if (StringUtil.isNotEmpty(namespace)) {
			ConfigHandlerProvider.getInstance().updateCache(Constant.PROJECR_NAMESPACE_NAME, namespace, Constant.DEFAULT_CONFIG_NAME);
		} 
		
		//Group(注册中心和dubbo)
		String group = System.getProperty(Constant.PROJECR_GROUP_NAME);
		if (StringUtil.isNotEmpty(group)) {
			ConfigHandlerProvider.getInstance().updateCache(Constant.PROJECR_GROUP_NAME, group, Constant.DEFAULT_CONFIG_NAME);
		} 

		//集群名称(注册中心和配置中心)
		String cluster = System.getProperty(Constant.PROJECR_CLUSTER_NAME);
		if (StringUtil.isNotEmpty(cluster)) {
			ConfigHandlerProvider.getInstance().updateCache(Constant.PROJECR_CLUSTER_NAME, cluster, Constant.DEFAULT_CONFIG_NAME);
		} 
		
		//服务端口
		String serverport = System.getProperty(Constant.SERVER_PORT_NAME);
		if (StringUtil.isNotEmpty(serverport)) {
			ConfigHandlerProvider.getInstance().updateCache(Constant.SERVER_PORT_NAME, serverport, Constant.DEFAULT_CONFIG_NAME);
		}
		
		//注册中心
		String name = System.getProperty(Constant.NAMING_REGISTRY_ADDRESS_NAME);
		if (StringUtil.isNotEmpty(name)) {
			ConfigHandlerProvider.getInstance().updateCache(Constant.NAMING_REGISTRY_ADDRESS_NAME, name, Constant.DEFAULT_CONFIG_NAME);
		}
		
		//dubbo服务端口
		String dubboport = System.getProperty(Constant.DUBBO_PROTOCOL_PORT_NAME);
		if (StringUtil.isNotEmpty(dubboport)) {
			ConfigHandlerProvider.getInstance().updateCache(Constant.DUBBO_PROTOCOL_PORT_NAME, dubboport, Constant.DEFAULT_CONFIG_NAME);
		}
		//dubbo注册中心
		String dubboname = System.getProperty(Constant.DUBBO_REGISTRY_ADDRESS_NAME);
		if (StringUtil.isNotEmpty(dubboname)) {
			ConfigHandlerProvider.getInstance().updateCache(Constant.DUBBO_REGISTRY_ADDRESS_NAME, dubboname, Constant.DEFAULT_CONFIG_NAME);
		}
		
    	// 启动字符集
    	System.setProperty("file.encoding", "UTF-8");
    	
		// user.home
        System.setProperty("user.home", PropertyUtils.getAppPath());

	}
}
