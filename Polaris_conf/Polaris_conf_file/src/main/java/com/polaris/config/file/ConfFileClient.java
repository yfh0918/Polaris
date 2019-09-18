package com.polaris.config.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.config.ConfListener;
import com.polaris.core.config.ConfigHandlerProvider;
import com.polaris.core.util.PropertyUtils;
import com.polaris.core.util.StringUtil;

import cn.hutool.core.thread.NamedThreadFactory;

public class ConfFileClient { 
	
	private static final Logger logger = LoggerFactory.getLogger(ConfFileClient.class);
	private volatile static ConfFileClient INSTANCE;
	//记录文件的最后的更新日期
	private static volatile Map<String, File> lastModifiedFileMap = new HashMap<>();
	private static volatile Map<String, Long> lastModifiedTimeMap = new HashMap<>();
	
	//定时器-守护线程
	private static ScheduledExecutorService service = Executors.newScheduledThreadPool(1,
            new NamedThreadFactory("polaris-localfile-auto-refresh-task", true));
	
	public static ConfFileClient getInstance(){
		if (INSTANCE == null) {
			synchronized(ConfFileClient.class) {
				if (INSTANCE == null) {
					INSTANCE = new ConfFileClient();
				}
			}
		}
		return INSTANCE;
	}
	private ConfFileClient() {
	}
	
	
	
	// 获取文件内容
	public String getConfig(String fileName, String group) {
		
		//可以监听的文件有效
		if (canListen(fileName)) {
			isModifiedByFile(fileName);
		}

		// propertyies
		if (fileName.toLowerCase().endsWith(".properties")) {
			return PropertyUtils.getPropertiesFileContent(ConfClient.getConfigFileName(fileName));
		}
		
		// 非propertyies
		try (InputStream inputStream = ConfigHandlerProvider.class.getClassLoader().getResourceAsStream(ConfClient.getConfigFileName(fileName))) {
			if (inputStream == null) {
				return null;
			}
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
        	logger.error("ConfigFile load error,ConfigFile is null");
        	e.printStackTrace();
        }
		
		return null;
	}
	
	// 监听需要关注的内容
	public void addListener(String fileName, String group, ConfListener listener) {
		//farjar或者配置文件放到jar包或者不存在配置文件的不用监听
		if (!canListen(fileName)) {
			return;
		}
		
		//配置文件存在的监听
		service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
            		
            		//是否修改过
            		if (isModifiedByFile(fileName)) {
            			listener.receive(getConfig(fileName, group));
            		}
                } catch (Throwable e) {
                	logger.error("addListener error:{}",e.getMessage());
                	e.printStackTrace();
                }
            }
        }, 10000, 10000, TimeUnit.MILLISECONDS);
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
				File file = new File(PropertyUtils.getFullPath(ConfClient.getConfigFileName(fileName)));
	    		lastModifiedFileMap.put(fileName, file);
				lastModifiedTimeMap.put(fileName, file.lastModified());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isModified;
	}
	
	//farjar或者配置文件放到jar包或者不存在配置文件的不用监听
	private static boolean canListen(String fileName) {
		try {
			File file = new File(PropertyUtils.getFullPath(ConfClient.getConfigFileName(fileName)));
			if (file.exists()) {
				return true;
			}
		} catch (IOException e) {
			logger.error("addListener error:{}",e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
		
}