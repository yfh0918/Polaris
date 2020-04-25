package com.polaris.container.gateway.pojo;

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
	
	 
}
