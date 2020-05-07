package com.polaris.core.pojo;

public class ServerHost {
	private static final String HTTP_PREFIX = "http://";
	private static final String HTTPS_PREFIX = "https://";
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
}
