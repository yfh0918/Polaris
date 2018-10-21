package com.polaris.comm.supports;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;

import com.polaris.comm.Constant;
import com.polaris.comm.config.ConfigHandlerProvider;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.PropertyUtils;
import com.polaris.comm.util.StringUtil;

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
	
	private static final LogUtil logger =  LogUtil.getInstance(MainSupport.class);

	
    /**
    * iniParameter(初期的参数配置)
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
	@SuppressWarnings("static-access")
	public static void iniParameter() {
		
		//配置中心(只能通过 -D或者application.properties文件配置)
		String config = System.getProperty(Constant.CONFIG_REGISTRY_ADDRESS_NAME);
		if (StringUtil.isEmpty(config)) {
			try {
				String propertyValue = PropertyUtils.readData(Constant.CONFIG + File.separator + Constant.DEFAULT_CONFIG_NAME, Constant.CONFIG_REGISTRY_ADDRESS_NAME, false);
				if (propertyValue != null) {
					System.setProperty(Constant.CONFIG_REGISTRY_ADDRESS_NAME, propertyValue);
				}
			} catch (Exception ex) {
				//nothing
			}
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
		
		//命名空间
		String namespace = System.getProperty(Constant.PROJECR_NAMESPACE_NAME);
		if (StringUtil.isNotEmpty(namespace)) {
			ConfigHandlerProvider.getInstance().updateCache(Constant.PROJECR_NAMESPACE_NAME, namespace, Constant.DEFAULT_CONFIG_NAME);
		} 
		
		//集群名称
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
	
	
	
    /**
    * configureAndWatch(日志配置以及监控)
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
    public static void configureAndWatch(long warchTime){
    	Thread run = new Thread(new Runnable(){
    		 @Override  
             public void run() {
                 String fileName = null;
				 try {
					fileName = PropertyUtils.getFilePath(Constant.CONFIG + File.separator + Constant.LOG4J);
				 } catch (IOException e) {
					logger.error(e);
				 }
                 File file = new File(fileName);
    			 long lastModified = 0L;
                 while(true){  
                	try {
                        long tempLastModified = file.lastModified();
                        if (lastModified != tempLastModified) {
                            lastModified = tempLastModified;
                            PropertyConfigurator.configure(MainSupport.class.getClassLoader().getResourceAsStream(Constant.CONFIG + File.separator + Constant.LOG4J));
                        }
						Thread.sleep(warchTime);
					} catch (InterruptedException e) {
						logger.error(e);
						Thread.currentThread().interrupt();
					} 
                 }
             }  
    	});
    	run.setDaemon(true);//守护线程
    	run.setName("ConfigureAndWatch Thread");
    	run.start();
    	try {
			Thread.sleep(100);//阻塞主线程100毫秒
		} catch (InterruptedException e) {
			logger.error(e);
			Thread.currentThread().interrupt();
		}
    }  
}
