package com.polaris.container.gateway.pojo;

import java.util.LinkedHashSet;
import java.util.Set;

import com.polaris.core.config.Config.Type;

public class HttpFilterFile {
	
	public HttpFilterFile(String name) {
		this.name = name;
		this.type = Type.EXT;
	}
	public HttpFilterFile(String name, Type type) {
		this.name = name;
		this.type = type;
	}
	private Type type;
	private String name;
	private Set<String> data;
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
	public Set<String> getData() {
		return data;
	}
	public void setData(Set<String> data) {
		if (data == null) {
			data = new LinkedHashSet<>();
		} else {
			this.data = data;
		}
	}
}
