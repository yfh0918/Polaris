package com.polaris.container.gateway.pojo;

import com.polaris.core.config.Config.Type;

public class HttpFile {
	
	public HttpFile(String name) {
		this.name = name;
		this.type = Type.EXT;
	}
	public HttpFile(String name, Type type) {
		this.name = name;
		this.type = type;
	}
	private Type type;
	private String name;
	private String data;
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
	    this.data = data;
	}
}
