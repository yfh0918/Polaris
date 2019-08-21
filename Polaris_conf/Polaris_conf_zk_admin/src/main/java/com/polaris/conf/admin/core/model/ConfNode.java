package com.polaris.conf.admin.core.model;

/**
 * 配置节点
 */
public class ConfNode {

	private String namespace;		// namespace
	private String group;		    // group of prop
	private String key;	            // group of prop
	private String value;
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
