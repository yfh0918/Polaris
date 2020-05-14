package com.polaris.container.gateway.pojo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Splitter;
import com.polaris.container.gateway.HttpConstant;
import com.polaris.container.gateway.HttpFileListener;
import com.polaris.container.gateway.HttpFileReader;
import com.polaris.core.pojo.KeyValuePair;
import com.polaris.core.util.PropertyUtil;
import com.polaris.core.util.StringUtil;

public class HttpHostContext implements HttpFileListener{

    public static final String NAME = "gw_host.txt";
    
    //key:gw_host_upstream.txt value:Set<HttpContextUpstream>
    private static volatile Map<String, Set<HttpContextUpstream>> contextFileMap = new ConcurrentHashMap<>();

    //key:127.0.0.1 value:gw_host_upstream.txt
    private static volatile Map<String, String> hostMap = new ConcurrentHashMap<>();
    
    //key:host+context value:HttpContextUpstream
    private static volatile Map<String, HttpContextUpstream> hostContextMap = new ConcurrentHashMap<>();

    public static void load(Set<String> contents) {
    	Map<String, String> temp_hostMap = new ConcurrentHashMap<>();
        for (String line : contents) {
        	KeyValuePair kv = PropertyUtil.getKVPair(line);
        	if (kv != null) {
        		if (StringUtil.isNotEmpty(kv.getValue())) {
        			//context-file不存在
        			if (!contextFileMap.containsKey(kv.getValue())) {
            			HttpHostContext hostContextUpstream = new HttpHostContext();
            			HttpFileReader.INSTANCE.readFile(hostContextUpstream, new HttpFile(kv.getValue()));
        			}
        			temp_hostMap.put(kv.getKey(), kv.getValue());
        		}
        	}
        }
        hostMap = temp_hostMap;
        
        //刷新
        refreshHostContextMap();
    }
    
    public static String getContextPath(String uri) {
    	List<String> contextList = Splitter.on(HttpConstant.SLASH).splitToList(uri);
 		return contextList.size() <= 2 ? HttpConstant.SLASH : HttpConstant.SLASH + contextList.get(1);
    }
    public static HttpContextUpstream get(String host, String context) {
    	return hostContextMap.get(host+context);
    }
    private static void refreshHostContextMap() {
    	Map<String, HttpContextUpstream> temp_hostContextMap = new ConcurrentHashMap<>();
    	for (Map.Entry<String, String> entry : hostMap.entrySet()) {
    		Set<HttpContextUpstream> contextSet = contextFileMap.get(entry.getValue());
    		if (contextSet != null) {
    			for (HttpContextUpstream contextUpstream : contextSet) {
    				temp_hostContextMap.put(entry.getKey()+contextUpstream.getContext(), contextUpstream);
    			}
    		}
    	}
    	hostContextMap = temp_hostContextMap;
    }

	@Override
    public void onChange(HttpFile file) {
		Set<String> contents = file.getData();
		if (contents == null || contents.size() == 0) {
			contextFileMap.remove(file.getName());
			return;
		}
		Set<HttpContextUpstream> contextUpstreamSet = new HashSet<>();
		for (String line : contents) {
        	KeyValuePair kv = PropertyUtil.getKVPair(line);
        	if (kv != null) {
        		if (StringUtil.isNotEmpty(kv.getKey()) && StringUtil.isNotEmpty(kv.getValue())) {
        			HttpContextUpstream contextUpstream = new HttpContextUpstream();
        			contextUpstream.setContext(kv.getKey());
        			contextUpstream.setServiceName(kv.getValue());
        			contextUpstreamSet.add(contextUpstream);
        		}
        	}
        }
		contextFileMap.put(file.getName(), contextUpstreamSet);
		
		//刷新
		refreshHostContextMap();
    }
	
	static public class HttpContextUpstream {
		private String context;//context，从requst的uri获取，没有为[/]
		private String serviceName;//反向代理的服务名称，可以是IP+port 或者 为注册中心或者DNS的名称
		public String getContext() {
			return context;
		}
		public void setContext(String context) {
			this.context = context;
		}
		public String getServiceName() {
			return serviceName;
		}
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}
	}
	
	/*
	public static void main( String[] args ) throws Exception
    {
		System.out.println(getContextPath(""));//return /
		System.out.println(getContextPath("/"));//return /
		System.out.println(getContextPath("/abc"));//return /
		System.out.println(getContextPath("/abc/"));//return /abc
		System.out.println(getContextPath("/abc/ass"));//return /abc
		System.out.println(getContextPath("/abc/ass/"));//return /abc
		System.out.println(getContextPath("/abc/ass/ada"));//return /abc
    }
    */
}
