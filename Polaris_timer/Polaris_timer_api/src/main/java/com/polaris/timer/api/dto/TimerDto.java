package com.polaris.timer.api.dto;

import java.io.Serializable;

import com.polaris.comm.dto.BaseDto;

public class TimerDto extends BaseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;//名称
	private String description;//描述
	private String schedule;//计划
	private String url;//执行路径
	private String enable;//是否可用
	private boolean isLikeSearch = false;//是否模糊查询
	private String token;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSchedule() {
		return schedule;
	}
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getEnable() {
		return enable;
	}
	public void setEnable(String enable) {
		this.enable = enable;
	}
	public boolean isLikeSearch() {
		return isLikeSearch;
	}
	public void setLikeSearch(boolean isLikeSearch) {
		this.isLikeSearch = isLikeSearch;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
