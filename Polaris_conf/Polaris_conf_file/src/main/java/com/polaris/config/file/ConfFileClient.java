package com.polaris.config.file;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.util.FileUtil;

import cn.hutool.core.thread.NamedThreadFactory;

public class ConfFileClient { 
	
	private static final Logger logger = LoggerFactory.getLogger(ConfFileClient.class);
	private volatile static ConfFileClient INSTANCE;
	//记录文件的最后的更新日期
	private static volatile Map<String, File> lastModifiedFileMap = new ConcurrentHashMap<>();
	private static volatile Map<String, Long> lastModifiedTimeMap = new ConcurrentHashMap<>();
	private static volatile Map<String, Set<ConfHandlerListener>> fileListeners = new ConcurrentHashMap<>();
	
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
		//配置文件存在的监听
		service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
            		
            		//是否修改过
                	for (Map.Entry<String, Set<ConfHandlerListener>> entry : fileListeners.entrySet()) {
                		if (isModifiedByFile(entry.getKey(),lastModifiedFileMap.get(entry.getKey()))) {
                			if (entry.getValue() != null) {
                				for (ConfHandlerListener listener : entry.getValue()) {
                					listener.receive(getConfig(entry.getKey(), null));
                				}
                			}
                			
                		}
                	}
            		
                } catch (Throwable e) {
                	logger.error("addListener error:{}",e.getMessage());
                	e.printStackTrace();
                }
            }
        }, 10000, 10000, TimeUnit.MILLISECONDS);
	}
	
	
	
	// 获取文件内容
	public String getConfig(String fileName, String group) {
		
		//可以监听的文件有效
		File file = FileUtil.getFile(fileName);
		if (file != null) {
			isModifiedByFile(fileName, file);
		}

		try {
			return FileUtil.read(FileUtil.getStream(fileName));
        } catch (IOException e) {
        	logger.error("ConfigFile load error,ConfigFile is null");
        	e.printStackTrace();
        }
		return null;
	}
	
	// 监听需要关注的内容
	public void addListener(String fileName, String group, ConfHandlerListener listener) {
		Set<ConfHandlerListener> set = fileListeners.get(fileName);
		if (set == null) {
			set = new LinkedHashSet<>();
			fileListeners.put(fileName, set);
		}
		set.add(listener);
	}

	//文件是否发生修改
	private boolean isModifiedByFile(String fileName, File file) {
		boolean isModified = true;
		if (lastModifiedFileMap.containsKey(fileName)) {
			file = lastModifiedFileMap.get(fileName);
			if (file.lastModified() == lastModifiedTimeMap.get(fileName).longValue()) {
				isModified = false;
			} else {
				lastModifiedTimeMap.put(fileName, file.lastModified());
			}
		} else {
			if (file != null) {
	    		lastModifiedFileMap.put(fileName, file);
				lastModifiedTimeMap.put(fileName, file.lastModified());
			}
		}
		return isModified;
	}
	
	
		
}