package com.polaris.container.gateway.pojo;

import com.polaris.container.gateway.HttpFilter;

import io.netty.handler.codec.http.HttpMessage;

public class HttpFilterEntity {
    private int order;  
    private HttpFilter<? extends HttpMessage> filter;
    private HttpFile[] files;

    public HttpFilterEntity(HttpFilter<? extends HttpMessage> filter, HttpFile... files) {
        this(filter, Integer.MIN_VALUE, files);
    }
    public HttpFilterEntity(HttpFilter<? extends HttpMessage> filter, int order, HttpFile... files) {  
        this.order = order; 
        this.files = files;
        this.setFilter(filter);
    }
	public HttpFilter<? extends HttpMessage> getFilter() {
		return this.filter;
	}
	public void setFilter(HttpFilter<? extends HttpMessage> filter) {
		this.filter = filter;
		this.filter.setHttpFilterEntity(this);
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public Integer getOrder() {
		return order;
	}
	public HttpFile[] getFiles() {
		return files;
	}
	public void setFiles(HttpFile... files) {
		this.files = files;
	}
	public void setOrder(int order) {
		this.order = order;
	}
}
