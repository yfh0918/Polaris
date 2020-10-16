package com.polaris.container.gateway.pojo;

import com.polaris.core.config.provider.Config.Type;

public class HttpFile {
	
	public HttpFile(String group, String name) {
	    this.group = group;
		this.name = name;
		this.type = Type.DEFAULT;
	}
	public HttpFile(String group, String name, Type type) {
	    this.group = group;
		this.name = name;
		this.type = type;
	}
	private Type type;
	private String group;
	private String name;
	private String data;
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
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
