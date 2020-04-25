package com.polaris.container.gateway.pojo;

import java.util.Set;

public class FileType {
	public enum Type {
		PATTERN,//patten匹配
		KV,//key value方式匹配
	}
	
	public FileType(String file, Type type) {
		this.file = file;
		this.type = type;
	}
	private String file;
	private Type type;
	private Set<String> data;
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Set<String> getData() {
		return data;
	}
	public void setData(Set<String> data) {
		this.data = data;
	}
}
