package com.polaris.comm.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaseDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 属于组件:all 作用：判断返回的操作是否成功 标准值：0-处理正常,1-处理失败,2-后台出错,3-权限不足,4-认证失败   前台是否传输：否 后台是否传输：是
	 */
	private Integer status;


	/**
	 * 消息内容
	 */
	private String msgContent;

	/**
	 * 请求参数Map
	 */
	private Map<String, Object> parameterMap = new LinkedHashMap<>();
	/**
	 * 获取map
	 */
	public Map<String, Object> getParameterMap() {
		return parameterMap;
	}
	public void setParameterMap(Map<String, Object> parameterMap) {
		this.parameterMap = parameterMap;
	}

	 public  BaseDto(){
		 
	 }
	 
	 public  BaseDto(Integer status,String msgContent){
	    	this.status = status;
	    	this.msgContent=msgContent;
	  }
	

	public Integer getStatus() {
		if(null==status){
			status=0;
		}
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	
}
