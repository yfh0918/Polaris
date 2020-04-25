package com.polaris.container.gateway.pojo;

import com.polaris.container.gateway.HttpFilter;

public class HttpFilterEntity {
	// 成员变量  
    private int order;  
    private String key;
    private HttpFilter filter;
    private FileType[] fileTypes;

    // 构造方法  
    public HttpFilterEntity(HttpFilter filter, String key, int order, FileType... fileTypes) {  
    	this.key = key;
        this.order = order; 
        this.filter = filter;
        this.fileTypes = fileTypes;
    }
	public HttpFilter getFilter() {
		return this.filter;
	}
	public void setFilter(HttpFilter filter) {
		this.filter = filter;
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
	public FileType[] getFileTypes() {
		return fileTypes;
	}
	public void setFileTypes(FileType... fileTypes) {
		this.fileTypes = fileTypes;
	}
	public void setOrder(int order) {
		this.order = order;
	}
}
