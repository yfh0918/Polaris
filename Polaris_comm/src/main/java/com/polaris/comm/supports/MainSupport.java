package com.polaris.comm.supports;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import com.polaris.comm.Constant;
import com.polaris.comm.config.ConfClient;
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
	private static final String config = "config";
	private static final String log4j_profix = "log4j.";
	private static final String log4j = log4j_profix + "properties";

	public static final long WARCH_TIME = 30000L;
	
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
					fileName = PropertyUtils.getFilePath(config + File.separator + log4j);
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
                            PropertyConfigurator.configure(MainSupport.class.getClassLoader().getResourceAsStream(config + File.separator + log4j));
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
			Thread.sleep(1000);//阻塞主线程1秒
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
    public static boolean makeSingle(String portName, String port) {
    	boolean result = false;
    	RandomAccessFile raf = null;
    	FileChannel channel = null;
    	FileLock lock = null;
        try {
        	
        	//端口号和名称
        	Constant.PORT_NAME = portName;
        	Constant.PORT = port;
        	
        	//获取应用名称和端口号
        	String name = ConfClient.getAppName();
        	if (StringUtil.isEmpty(port)) {
        		throw new NullPointerException("the port is null");
        	}
        	String key = name + port;
            String fileName = System.getProperty("java.io.tmpdir") + key;
            
            // 在临时文件夹创建一个临时文件，锁住这个文件用来保证应用程序只有一个实例被创建.
            File pinfile = new File(fileName);
            pinfile.delete();
            if(!pinfile.createNewFile()){
            	logger.error("创建文件 失败!");
            }
            File sf = new File(fileName+ ".single");
            sf.delete();
            if(!sf.createNewFile()){
            	logger.error("创建文件 失败!");
            }
            raf = new RandomAccessFile(sf, "rw");
            channel = raf.getChannel();
            lock = channel.tryLock();
            if (lock == null) {
            	result = true; 
            	logger.error("端口号 ："+port+"已经被占用");
            }
            
            //记录pin
            if (!result) {
                String runtimeName = ManagementFactory.getRuntimeMXBean().getName();    
                String pid = runtimeName.split("@")[0];
            	PropertyUtils.writeData(fileName, "pid", pid, true);                
            }
        } catch (Exception e) {
        	logger.error("makeSingle异常", e);
        }
        
        return result;
    }
    
    
    /**
    * getPidListFromJps(根据JPS命令获取pid列表)
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
    public static List<String> getPidListFromJps(String jdkpath) {
    	List<String> result = new ArrayList<>();
    	try {
    		String cmd = jdkpath + File.separator + "bin" + File.separator + "jps -l";
            Process p = Runtime.getRuntime().exec(cmd);
            InputStream is = p.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
            	result.add(line);
            }
            p.waitFor();
            is.close();
            reader.close();
            p.destroy();
            
    	} catch (Exception ex) {
           	logger.error("getPidListFromJps异常", ex);
    	} 
    	return result;
    }
    
    
    /**
    * getPidFromJps(根据JPS命令获取pid)
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
    public static String getPidFromJps(List<String> pidList, String key) {
    	if (pidList == null || pidList.isEmpty()) {
    		return null;
    	}
    	String result = null;
    	for (String pid : pidList) {
    		if (pid != null && pid.contains(key)) {
        		result = pid.substring(0, pid.indexOf(' '));
        		break;
        	}
    	}
    	return result;
    }
    
    
    /**
    * getPidFromFile(根据文件命令获取pid)
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
    public static String getPidFromFile(String key) {
        RandomAccessFile raf = null;
        FileChannel channel = null;
        FileLock lock = null;
 
        String fileName = System.getProperty("java.io.tmpdir") + key;
        try {
            // 在临时文件夹创建一个临时文件，锁住这个文件用来保证应用程序只有一个实例被创建.
            File sf = new File(fileName+ ".single");
            raf = new RandomAccessFile(sf, "rw");
            channel = raf.getChannel();
            lock = channel.tryLock();
            
            //正在使用返回pid
            if (lock == null) {
            	return PropertyUtils.readData(fileName, "pid", true);             
            } 
        } catch (Exception e) {
           	logger.error("getPidFromFile异常", e);
        } finally {
        	if (lock != null) {
        		try {
                	lock.release();
        		} catch (Exception ex) {
                   	logger.error("getPidFromFile异常", ex);
        		}
            	lock = null;
            }
        	if (raf != null) {
        		try {
        			raf.close();
        		} catch (Exception ex) {
                   	logger.error("getPidFromFile异常", ex);
        		}
        		raf = null;
            }
        }
        
        return null;
    }
}
