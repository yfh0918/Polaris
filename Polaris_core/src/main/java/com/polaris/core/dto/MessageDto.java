package com.polaris.core.dto;

import java.io.Serializable;

public class MessageDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 属于组件，状态值
	 */
	private String status;


	/**
	 * 消息内容
	 */
	private String msgContent;

	 public  MessageDto(){
		 
	 }
	 
	 public  MessageDto(String status,String msgContent){
	    	this.status = status;
	    	this.msgContent=msgContent;
	  }
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	
}
