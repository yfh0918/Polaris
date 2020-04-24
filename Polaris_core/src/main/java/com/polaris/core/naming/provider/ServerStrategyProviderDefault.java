package com.polaris.core.naming.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.polaris.core.util.StringUtil;

public class ServerStrategyProviderDefault implements ServerStrategyProvider{
	private static final String HTTP_PREFIX = "http://";
	private static final String HTTPS_PREFIX = "https://";
	private static final String IP_REXP = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";  
    public static final ServerStrategyProviderDefault INSTANCE = new ServerStrategyProviderDefault();
    private static final ServerHandlerRemoteProvider INSTANCE_REMOTE = ServerHandlerRemoteProvider.INSTANCE;
    private static final ServerHandlerLocalProvider INSTANCE_LOCAL = ServerHandlerLocalProvider.INSTANCE;
    private ServerStrategyProviderDefault() {}
    
    @Override
    public boolean register(String ip, int port) {
    	return INSTANCE_REMOTE.register(ip, port);
    }
    
    @Override
    public boolean deregister(String ip, int port) {
    	return INSTANCE_REMOTE.deregister(ip, port);
    }
    
    @Override
	public String getUrl(String key) {
    	String url = null;
    	List<String> serverInfoList = parseServer(key);
		if (!isIP(serverInfoList.get(1))) {
			url = INSTANCE_REMOTE.getUrl(key,serverInfoList);
		}
		return url == null ? INSTANCE_LOCAL.getUrl(key,serverInfoList) : url;
	}

    @Override
	public List<String> getAllUrls(String key) {
		List<String> urls = null;
		List<String> serverInfoList = parseServer(key);
		if (!isIP(serverInfoList.get(1))) {
			urls = INSTANCE_REMOTE.getAllUrl(key,serverInfoList);
		}
		return urls == null ? INSTANCE_LOCAL.getAllUrl(key,serverInfoList) : urls;
	}
	
    @Override
	public List<String> getAllUrls(String key, boolean subscribe) {
		List<String> urls = null;
		List<String> serverInfoList = parseServer(key);
		if (!isIP(serverInfoList.get(1))) {
			urls = INSTANCE_REMOTE.getAllUrl(key,serverInfoList,subscribe);
		}
		return urls == null ? INSTANCE_LOCAL.getAllUrl(key,serverInfoList,subscribe) : urls;
	}

    @Override
	public boolean connectionFail(String key, String url) {
		List<String> serverInfoList = parseServer(key);
		List<String> serverInfoList2 = parseServer(url);
		if (!isIP(serverInfoList.get(1))) {
			return INSTANCE_REMOTE.connectionFail(serverInfoList.get(1), serverInfoList2.get(1));
		} 
		return INSTANCE_LOCAL.connectionFail(serverInfoList.get(1), serverInfoList2.get(1));
	}
	
    @Override
	public void init() {
		INSTANCE_LOCAL.init();
	}
	
	public boolean isIP(String addr) {  
        if(StringUtil.isEmpty(addr) || addr.length() < 7 || addr.length() > 15) {  
            return false;  
        }  
        Pattern pat = Pattern.compile(IP_REXP);    
        Matcher mat = pat.matcher(addr);    
        boolean ipAddress = mat.find();  
        return ipAddress;  
    }  
	
	private List<String> parseServer(String serverInfo) {
		List<String> serverList = new ArrayList<>(3);
		if (serverInfo.toLowerCase().startsWith(HTTP_PREFIX)) {
			serverList.add(HTTP_PREFIX);
			serverInfo = serverInfo.substring(HTTP_PREFIX.length());
		} else if (serverInfo.toLowerCase().startsWith(HTTPS_PREFIX)) {
			serverList.add(HTTPS_PREFIX);
			serverInfo = serverInfo.substring(HTTPS_PREFIX.length());
		} else {
			serverList.add("");
		}
		int suffixIndex = serverInfo.indexOf("/");
		if (suffixIndex > 0) {
			serverList.add(serverInfo.substring(0, suffixIndex));
			serverList.add(serverInfo.substring(suffixIndex));
		} else {
			serverList.add(serverInfo);
			serverList.add("");
		}
        return serverList;
    }

	 public static void main(String[] args)   
	    {  
	        /** 
	         * 符合IP地址的范围 
	         */  
	         String oneAddress = "10.128.36.88";  
	         /** 
	         * 符合IP地址的长度范围但是不符合格式 
	         */  
	         String twoAddress = "127.34.75";  
	         /** 
	         * 不符合IP地址的长度范围 
	         */  
	         String threeAddress = "5.0.8";  
	         /** 
	         * 不符合IP地址的长度范围但是不符合IP取值范围 
	         */  
	         String fourAddress = "255.255.255.2347";  
	         ServerStrategyProviderDefault ipAdd = new ServerStrategyProviderDefault();  
	         System.out.println(ipAdd.isIP(oneAddress));  
	         System.out.println(ipAdd.isIP(twoAddress));  
	         System.out.println(ipAdd.isIP(threeAddress));  
	         System.out.println(ipAdd.isIP(fourAddress));  
	    }  

}
