package com.polaris.container.gateway;

public class HttpFilterEntity {
	// 成员变量  
    private int order;  
    private String key;
    private Class<? extends HttpFilter> clazz;
    private HttpFilter filter;

    // 构造方法  
    public HttpFilterEntity(HttpFilter filter, String key, int order) {  
    	this.clazz = filter.getClass();
    	this.key = key;
        this.order = order; 
        this.filter = filter;
    }
	public HttpFilter getFilter() {
		return this.filter;
	}
	public void setFilter(HttpFilter filter) {
		this.filter = filter;
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
