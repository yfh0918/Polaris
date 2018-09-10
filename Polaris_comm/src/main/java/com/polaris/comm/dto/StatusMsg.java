package com.polaris.comm.dto;

public class StatusMsg {
	
	private Integer status;
	
	private String msgContent;

	public StatusMsg() {
		super();
	}
	
	public StatusMsg(Integer status,String msgContent) {
		this.status=status;
		this.msgContent = msgContent;
	}
	
	public Integer getStatus() {
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
