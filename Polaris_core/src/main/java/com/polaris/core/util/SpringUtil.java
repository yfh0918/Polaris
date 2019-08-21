package com.polaris.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {
	private static ApplicationContext context = null;
	public static final String SPRING_PATH = "META-INF/spring/applicationContext.xml";
	
	/*

     * 实现了ApplicationContextAware 接口，必须实现该方法；

     *通过传递applicationContext参数初始化成员变量applicationContext

     */
    @SuppressWarnings("static-access")
    @Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    	this.context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
           return context;
    }
    
    public static Object getBean(String serviceName){
    	if (context == null) {
    		return null;
    	}
    	try {
            return context.getBean(serviceName);
    	} catch (Exception ex) {
    		return null;
    	}
    }

    public static <T> T getBean(Class<T> requiredType){
    	if (context == null) {
    		return null;
    	}
    	try {
        	return context.getBean(requiredType);
    	} catch (Exception ex) {
    		return null;
    	}
    }

    public static void refresh() {
    	if (context == null) {
    		return;
    	}
    	synchronized(SpringUtil.class) {
    		((AbstractApplicationContext)context).refresh();    	
    	}
    }  

//    //获取注册在zookeeper上的服务提供者，根据类名和IP 比如 className=com.polaris_auth_api.service.AuthService
//    public static List<String> getDubboBeanUrls(Class<?> requiredType) {
//		try {
//	    	String path = "/dubbo/"+requiredType.getName()+"/providers";
//	    	Stat stat = ConfZkClient.getInstance().exists(path, false);
//			if (stat != null) {
//				List<String> list = ConfZkClient.getInstance().getChildren(path, false);
//				refreshDubboBeans(requiredType,list);
//				return list;
//			} 
//		} catch (Exception e) {
//			//nothing
//		}
//		Map<String, ReferenceBean<?>> map = referenceBeanMap.get(requiredType.getName());
//		if (map == null || map.size() == 0) {
//			return null;
//		}
//		return new ArrayList<String>(map.keySet());
//    }
//    
//    //获取注册的dubbo类
//    //获取注册在zookeeper上的服务提供者，ip=192.168.16.90:9005
//    @SuppressWarnings("unchecked")
//	public static <T> T getDubboBean(String url, Class<T> requiredType) throws Exception {
//    	if (StringUtil.isEmpty(url) || context == null) {
//    		return null;
//    	}
//    	Map<String, ReferenceBean<?>> map = referenceBeanMap.get(requiredType.getName());
//    	if (map == null || map.get(url) == null) {
//    		return null;
//    	}
//		return (T)map.get(url).get();
//    }
//    
//    //获取的url进行转换
//    public static String getDubboUrl(String url) {
//    	url = url.replaceAll("%3A%2F%2F", "://");
//    	url = url.replaceAll("%3A", ":");
//    	url = url.replaceAll("%2F", "/");
//    	url = url.replaceAll("%3F", "?");
//    	url = url.replaceAll("%3D", "=");
//    	url = url.replaceAll("%26", "&");
//    	return url;
//    }
//    
//    //注销referenceBean
//    private static void refreshDubboBeans(Class<?> requiredType, List<String> includeUrls) throws Exception {
//    	
//    	//没有url
//    	Map<String, ReferenceBean<?>> map = referenceBeanMap.get(requiredType.getName());
//    	
//    	//集群中所有的服务都没有的可能性为0
//		if (includeUrls == null || includeUrls.size() == 0) {
//			return;
//			/*
//	    	synchronized(requiredType.getName().intern()) {
//	    		if (map != null) {
//	        		for(Map.Entry<String, ReferenceBean<?>> entry : map.entrySet()) {    
//	        			entry.getValue().destroy();
//	        		}
//	        		map.clear();
//	        		referenceBeanMap.remove(requiredType.getName());
//	    		}
//        		return;
//	    	}
//	    	*/
//    	}
//
//		//重新查询没有任何记录则删除原有全部的记录
//    	if (map == null) {
//    		synchronized(requiredType.getName().intern()) {
//    			if (map == null) {
//    				map = new ConcurrentHashMap<>();
//    	    		referenceBeanMap.put(requiredType.getName(), map);
//    			}
//    		}
//    	}
//		
//    	//循环更新Map
//    	for(Map.Entry<String, ReferenceBean<?>> entry : map.entrySet()) { 
//    		if (!includeUrls.contains(entry.getKey())) {
//    			entry.getValue().destroy();
//    			map.remove(entry.getKey());
//    		}
//		}
//		for (String url : includeUrls) {
//			if (!map.containsKey(url)) {
//				String dubboUrl = getDubboUrl(url);
//    	        ReferenceBean<?> referenceBean = new ReferenceBean<>();  
//    	        referenceBean.setApplicationContext(context);  
//    	        referenceBean.setInterface(requiredType);  
//    	        referenceBean.setUrl(dubboUrl);  
//    	        referenceBean.afterPropertiesSet(); 
//    	        map.put(url, referenceBean);
//			}
//		}
//    }
}
