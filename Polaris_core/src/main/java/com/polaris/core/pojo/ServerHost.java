package com.polaris.core.pojo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.polaris.core.util.StringUtil;

public class ServerHost {
	private static final String HTTP_PREFIX = "http://";
	private static final String HTTPS_PREFIX = "https://";
	private static final String IP_REXP = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";  
	private static final Pattern IP_PAT = Pattern.compile(IP_REXP);    

	private String prefix;
	private String serviceName;
	private String uri;
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public static ServerHost of(String url) {
		//example:url = http://myservice/abc/cc?aa=bb
		ServerHost serverHost = new ServerHost();
		if (url.toLowerCase().startsWith(HTTP_PREFIX)) {
			serverHost.setPrefix(HTTP_PREFIX);
			url = url.substring(HTTP_PREFIX.length());
		} else if (url.toLowerCase().startsWith(HTTPS_PREFIX)) {
			serverHost.setPrefix(HTTPS_PREFIX);
			url = url.substring(HTTPS_PREFIX.length());
		} else {
			serverHost.setPrefix("");
		}
		int suffixIndex = url.indexOf("/");
		if (suffixIndex > 0) {
			serverHost.setServiceName(url.substring(0, suffixIndex));
			serverHost.setUri(url.substring(suffixIndex));
		} else {
			serverHost.setServiceName(url);
			serverHost.setUri("");
		}
        return serverHost;
    }
	public static boolean isIp(String serviceName) {
		//example 192.168.1.1,192.168.2.2 (多IP用,隔离)
		for (String ipAndPort : Splitter.on(",").omitEmptyStrings().splitToList(serviceName)) {
			try {
				//example 192.169.2.2:8081 or 192.169.2.2:8081:1
				Server server = Server.of(ipAndPort);
				if (server == null) {
					return false;
				}
				if(StringUtil.isEmpty(server.getIp()) || server.getIp().length() < 7 || server.getIp().length() > 15) {  
		            return false;  
		        }  
		        Matcher mat = IP_PAT.matcher(server.getIp());    
		        if (!mat.find()) {
		        	return false;
		        }
			} catch (Exception ex) {
				return false;
			}
		}
		return true;
	}
}
