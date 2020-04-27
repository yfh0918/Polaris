package com.polaris.container.gateway.pojo;

import java.util.LinkedHashSet;
import java.util.Set;

public class HttpFilterFile {
	
	public HttpFilterFile(String name) {
		this.name = name;
	}
	private String name;
	private Set<String> data;
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
