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
    
    /**
    * makeSingle(服务是否重复启动判断)
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
//    public static boolean makeSingle() {
//    	boolean result = false;
//    	RandomAccessFile raf = null;
//    	FileChannel channel = null;
//    	FileLock lock = null;
//        try {
//        	
//        	//获取应用名称和端口号
//        	String name = ConfClient.get(Constant.PROJECT_NAME, false);
//        	String port = ConfClient.get(Constant.SERVER_PORT_NAME, false);
//        	if (StringUtil.isEmpty(port)) {
//        		port = ConfClient.get(Constant.DUBBO_PROTOCOL_PORT_NAME, false);
//        	}
//        	if (StringUtil.isEmpty(port)) {
//        		throw new NullPointerException("the port is null");
//        	}
//        	String key = name + port;
//            String fileName = System.getProperty("java.io.tmpdir") + key;
//            
//            // 在临时文件夹创建一个临时文件，锁住这个文件用来保证应用程序只有一个实例被创建.
//            File pinfile = new File(fileName);
//            pinfile.delete();
//            if(!pinfile.createNewFile()){
//            	logger.error("创建文件 失败!");
//            }
//            File sf = new File(fileName+ ".single");
//            sf.delete();
//            if(!sf.createNewFile()){
//            	logger.error("创建文件 失败!");
//            }
//            raf = new RandomAccessFile(sf, "rw");
//            channel = raf.getChannel();
//            lock = channel.tryLock();
//            if (lock == null) {
//            	result = true; 
//            	logger.error("端口号 ："+port+"已经被占用");
//            }
//            
//            //记录pin
//            if (!result) {
//                String runtimeName = ManagementFactory.getRuntimeMXBean().getName();    
//                String pid = runtimeName.split("@")[0];
//            	PropertyUtils.writeData(fileName, "pid", pid, true);                
//            }
//        } catch (Exception e) {
//        	logger.error("makeSingle异常", e);
//        }
//        
//        return result;
//    }
    
    
//    /**
//    * getPidListFromJps(根据JPS命令获取pid列表)
//    * @param 
//    * @return 
//    * @Exception 
//    * @since 
//    */
//    public static List<String> getPidListFromJps(String jdkpath) {
//    	List<String> result = new ArrayList<>();
//    	try {
//    		String cmd = jdkpath + File.separator + "bin" + File.separator + "jps -l";
//            Process p = Runtime.getRuntime().exec(cmd);
//            InputStream is = p.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            String line;
//            while ((line = reader.readLine()) != null) {
//            	result.add(line);
//            }
//            p.waitFor();
//            is.close();
//            reader.close();
//            p.destroy();
//            
//    	} catch (Exception ex) {
//           	logger.error("getPidListFromJps异常", ex);
//    	} 
//    	return result;
//    }
//    
//    
//    /**
//    * getPidFromJps(根据JPS命令获取pid)
//    * @param 
//    * @return 
//    * @Exception 
//    * @since 
//    */
//    public static String getPidFromJps(List<String> pidList, String key) {
//    	if (pidList == null || pidList.isEmpty()) {
//    		return null;
//    	}
//    	String result = null;
//    	for (String pid : pidList) {
//    		if (pid != null && pid.contains(key)) {
//        		result = pid.substring(0, pid.indexOf(' '));
//        		break;
//        	}
//    	}
//    	return result;
//    }
//    
//    
//    /**
//    * getPidFromFile(根据文件命令获取pid)
//    * @param 
//    * @return 
//    * @Exception 
//    * @since 
//    */
//    public static String getPidFromFile(String key) {
//        RandomAccessFile raf = null;
//        FileChannel channel = null;
//        FileLock lock = null;
// 
//        String fileName = System.getProperty("java.io.tmpdir") + key;
//        try {
//            // 在临时文件夹创建一个临时文件，锁住这个文件用来保证应用程序只有一个实例被创建.
//            File sf = new File(fileName+ ".single");
//            raf = new RandomAccessFile(sf, "rw");
//            channel = raf.getChannel();
//            lock = channel.tryLock();
//            
//            //正在使用返回pid
//            if (lock == null) {
//            	return PropertyUtils.readData(fileName, "pid", true);             
//            } 
//        } catch (Exception e) {
//           	logger.error("getPidFromFile异常", e);
//        } finally {
//        	if (lock != null) {
//        		try {
//                	lock.release();
//        		} catch (Exception ex) {
//                   	logger.error("getPidFromFile异常", ex);
//        		}
//            	lock = null;
//            }
//        	if (raf != null) {
//        		try {
//        			raf.close();
//        		} catch (Exception ex) {
//                   	logger.error("getPidFromFile异常", ex);
//        		}
//        		raf = null;
//            }
//        }
//        
//        return null;
//    }
}
