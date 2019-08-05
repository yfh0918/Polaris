package com.polaris.core.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.pagehelper.util.StringUtil;
import com.polaris.core.Constant;
import com.polaris.core.util.PropertyUtils;

import cn.hutool.core.thread.NamedThreadFactory;

public class ConfHandlerSupport {

	//定时器-守护线程
	private static ScheduledExecutorService service = Executors.newScheduledThreadPool(1,
            new NamedThreadFactory("polaris-localfile-auto-refresh-task", true));
	
	//记录文件的最后的更新日期
	private static volatile Map<String, File> lastModifiedFileMap = new HashMap<>();
	private static volatile Map<String, Long> lastModifiedTimeMap = new HashMap<>();


	/**
	* 获取扩展配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String[] getExtensionProperties() {
		try {
			//从本地获取
			String files = PropertyUtils.readData(Constant.PROJECT_PROPERTY, Constant.PROJECT_EXTENSION_PROPERTIES, false);
			return files.split(",");
		} catch (Exception ex) {
			//nothing
		}
		return null;
	}
	
	/**
	* 获取全局配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String[] getGlobalProperties() {
		try {
			//从本地获取
			String files = PropertyUtils.readData(Constant.PROJECT_PROPERTY, Constant.PROJECT_GLOBAL_PROPERTIES, false);
			return files.split(",");
		} catch (Exception ex) {
			//nothing
		}
		return null;
	}
	
	/**
	* 获取KV对
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String[] getKeyValue(String line) {
		if (StringUtil.isNotEmpty(line)) {
			String[] keyvalue = line.split("=");
			if (keyvalue.length == 0) {
				return new String[] {"",""};
			}
			if (keyvalue.length == 1) {
				return new String[] {keyvalue[0].trim(),""};
			}
			String value = "";
			for (int index = 0; index < keyvalue.length; index++) {
				if (index != 0) {
					if (StringUtil.isEmpty(value)) {
						value = keyvalue[index].trim();
					} else {
						value = value + "=" + keyvalue[index].trim();
					}
				}
			}
			return new String[] {keyvalue[0].trim(),value};
		}
		return null;
	}
	
	/**
	* 获取整个文件的内容
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	@SuppressWarnings("rawtypes")
	public static String getLocalFileContent(String fileName) {
		
		//更新文件
		isModifiedByFile(fileName);

		// propertyies
		if (fileName.toLowerCase().endsWith(".properties")) {
			StringBuffer buffer = new StringBuffer();
			try (InputStream in = ConfigHandlerProvider.class.getClassLoader().getResourceAsStream(Constant.CONFIG + File.separator + fileName)) {
	            Properties p = new Properties();
	            p.load(in);
	            for (Map.Entry entry : p.entrySet()) {
	                String key = (String) entry.getKey();
	                buffer.append(key + "=" + entry.getValue());
	                buffer.append(Constant.LINE_SEP);
	            }
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
			return buffer.toString();
		}
		
		// 非propertyies
		try (InputStream inputStream = ConfigHandlerProvider.class.getClassLoader().getResourceAsStream(Constant.CONFIG + File.separator + fileName)) {
			InputStreamReader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
			BufferedReader bf= new BufferedReader(reader);
			StringBuffer buffer = new StringBuffer();
			String line = bf.readLine();
	        while (line != null) {
	        	buffer.append(line);
	            line = bf.readLine();
	        	buffer.append(Constant.LINE_SEP);
	        }
	        String content = buffer.toString();
	        if (StringUtil.isNotEmpty(content)) {
	        	return content;
	        }
        } catch (IOException e) {
        	e.printStackTrace();
        }
		
		return null;
	}
	
	/**
	* 启动监听线程
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static void listionLocalFile(String fileName, ConfListener listener, long recommendRefresh, TimeUnit timeUnit) {
		
	        service.scheduleAtFixedRate(new Runnable() {
	            @Override
	            public void run() {
	                try {
	            		
	            		//是否修改过
	            		if (isModifiedByFile(fileName)) {
	            			listener.receive(getLocalFileContent(fileName));
	            		}
	                } catch (Throwable e) {
	                	e.printStackTrace();
	                }
	            }
	        }, recommendRefresh, recommendRefresh, timeUnit);
	}
	
	//文件是否发生修改
	private static boolean isModifiedByFile(String fileName) {
		boolean isModified = true;
		if (lastModifiedFileMap.containsKey(fileName)) {
			File file = lastModifiedFileMap.get(fileName);
			if (file.lastModified() == lastModifiedTimeMap.get(fileName).longValue()) {
				isModified = false;
			} else {
				lastModifiedTimeMap.put(fileName, file.lastModified());
			}
		} else {
			try {
				File file = new File(PropertyUtils.getFilePath(Constant.CONFIG + File.separator + fileName));
	    		lastModifiedFileMap.put(fileName, file);
				lastModifiedTimeMap.put(fileName, file.lastModified());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isModified;
	}
	
	//获取分组名称
	public static String getGroup(boolean isGlobal) {
		if (isGlobal) {
			return getGlobalConfigGroup();
		}
		return getConfigGroup();
	}
	// 获取配置中心的分组
	public static String getConfigGroup() {
		StringBuilder group = new StringBuilder();
		if (StringUtil.isNotEmpty(ConfClient.getEnv())) {
			group.append(ConfClient.getEnv());
			group.append(":");
		}
		if (StringUtil.isNotEmpty(ConfClient.getCluster())) {
			group.append(ConfClient.getCluster());
			group.append(":");
		}
		group.append(ConfClient.getAppName());
		return group.toString();
	}
	
	// 获取全局配置中心的分组
	public static String getGlobalConfigGroup() {
		StringBuilder group = new StringBuilder();
		if (StringUtil.isNotEmpty(ConfClient.getEnv())) {
			group.append(ConfClient.getEnv());
			group.append(":");
		}
		if (StringUtil.isNotEmpty(ConfClient.getCluster())) {
			group.append(ConfClient.getCluster());
			group.append(":");
		}
		group.append(ConfClient.getGlobalGroup());
		return group.toString();
	}
}
