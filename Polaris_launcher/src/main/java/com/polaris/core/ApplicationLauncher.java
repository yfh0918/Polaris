package com.polaris.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import com.polaris.core.annotation.PolarisApplication;

public class ApplicationLauncher {
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationLauncher.class);
	
	public static void main(String[] args) throws IOException {
		ApplicationLauncher.scanMainClass(args);
	}
	
    private static void scanMainClass(String[] args) throws IOException {  
    	logger.info("start class scan ...");
    	URL url = ClassUtils.getDefaultClassLoader().getResource("");
    	String path = java.net.URLDecoder.decode(url.getPath(),Charset.defaultCharset().name());
    	File directory = new File(path);
    	File file = null;
		if(directory.isDirectory()){
			File [] filelist =directory.listFiles();
			for (File tempfile : filelist) {
				if (tempfile.getName().endsWith(".jar")) {
					file = tempfile;
					break;
				}
			}
		}
		if (file == null) {
			logger.error("jar file has not found");
		}
		try(JarFile jarFile = new JarFile(file)) {
			for(Enumeration<JarEntry> enumeration =  jarFile.entries(); enumeration.hasMoreElements(); ) {
	            JarEntry jarEntry = enumeration.nextElement();
	            String className = jarEntry.getName();
	            try {
	            	if (className.endsWith(".class")) {
	            		if (className.endsWith(".class")) {
	            	    	className = className.substring(0, className.length() - 6);
	                    }
	                	className = className.replace('/', '.');
	                	Class<?> startClass =Class.forName(className);
	                	//获取注解
	                	if (startClass.isAnnotationPresent(PolarisApplication.class) || Launcher.class.isAssignableFrom(startClass)) {
	                		logger.info("startup class:{} is found",className);
	                		Method method = startClass.getMethod("main", String[].class);
	            			method.invoke(null, (Object)args);
	            			return;
	                	}
	            	}
	            } catch (Exception e) {
					logger.error("Error",e);
	            }
	        }
		} catch (Exception e) {
			logger.error("Error",e);
			return;
		}
		logger.error("in jar:{} has not startup class",file.getName());
	}
}
