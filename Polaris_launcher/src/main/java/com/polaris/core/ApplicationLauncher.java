package com.polaris.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.ClassReader;

import com.polaris.core.annotation.PolarisApplication;
import com.polaris.core.annotation.PolarisGatewayApplication;

public class ApplicationLauncher {
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationLauncher.class);
	private static final String DOT_CLASS = ".class";
	private static final String MANIFEST_FILE = "META-INF/MANIFEST.MF";
	
	public static void main(String[] args) throws IOException {
		ApplicationLauncher.scanMainClass(args);
	}
	
    private static void scanMainClass(String[] args) throws IOException {  
    	URL url = ApplicationLauncher.class.getClassLoader().getResource(MANIFEST_FILE);
    	String path = java.net.URLDecoder.decode(url.getPath(),Charset.defaultCharset().name());
		path = path.substring(5).split("!")[0];
    	File file = new File(path);
		try(JarFile jarFile = new JarFile(file)) {
			for(Enumeration<JarEntry> enumeration =  jarFile.entries(); enumeration.hasMoreElements(); ) {
	            JarEntry jarEntry = enumeration.nextElement();
	            if (jarEntry.getName().endsWith(DOT_CLASS)) {
	            	try (InputStream inputStream = new BufferedInputStream(jarFile.getInputStream(jarEntry))) {
	    				ClassDescriptor classDescriptor = createClassDescriptor(inputStream);
	    				if (classDescriptor != null && 
	    					classDescriptor.isMainMethodFound() && 
	    					(classDescriptor.getAnnotationNames().contains(PolarisApplication.class.getName()) ||
	    					 classDescriptor.getAnnotationNames().contains(PolarisGatewayApplication.class.getName()))) {
	    					String className = convertToClassName(jarEntry.getName());
	    					logger.info("startup class:{} is found",className);
	    					Class<?> startClass =Class.forName(className);
	                		Method method = startClass.getMethod("main", String[].class);
	            			method.invoke(null, (Object)args);
	            			return;
	    				}
	    			} catch (Exception e) {
						logger.error("Error",e);
		            }
	            }
	        }
		} catch (Exception e) {
			logger.error("Error",e);
			return;
		}
		logger.error("in jar:{} has not startup class",file.getName());
	}
    
    private static ClassDescriptor createClassDescriptor(InputStream inputStream) {
		try {
			ClassReader classReader = new ClassReader(inputStream);
			ClassDescriptor classDescriptor = new ClassDescriptor();
			classReader.accept(classDescriptor, ClassReader.SKIP_CODE);
			return classDescriptor;
		} catch (IOException ex) {
			logger.error("ERROR:",ex);
			return null;
		}
	}
    
    private static String convertToClassName(String name) {
		name = name.replace('/', '.');
		name = name.replace('\\', '.');
		name = name.substring(0, name.length() - DOT_CLASS.length());
		return name;
	}
}
