package com.polaris.container.gateway;

public class HttpFilterObject {
	// 成员变量  
    private int order;  
    private String key;
    private Class<? extends HttpFilter> clazz;
    

    // 构造方法  
    public HttpFilterObject(Class<? extends HttpFilter> clazz, String key, int order) {  
    	this.clazz = clazz;
    	this.key = key;
        this.order = order;   
    }
	public Class<? extends HttpFilter> getClazz() {
		return this.clazz;
	}
	public void setClazz(Class<? extends HttpFilter> clazz) {
		this.clazz = clazz;
	}
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public Integer getOrder() {
		return order;
	}
}
