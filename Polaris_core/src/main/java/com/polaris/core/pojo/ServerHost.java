package com.polaris.core.pojo;

public class ServerHost {
	public static final String HTTP_PREFIX = "http://";
	public static final String HTTPS_PREFIX = "https://";
	public static final String WS_PREFIX = "ws://";
	public static final String WSS_PREFIX = "wss://";
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
        } else if (url.toLowerCase().startsWith(WS_PREFIX)) {
            serverHost.setPrefix(WS_PREFIX);
            url = url.substring(WS_PREFIX.length());
        } else if (url.toLowerCase().startsWith(WSS_PREFIX)) {
            serverHost.setPrefix(WSS_PREFIX);
            url = url.substring(WSS_PREFIX.length());
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
}
