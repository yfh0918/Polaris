package com.polaris.core.pojo;

import java.util.Map;
/**
 * EmailDto
 *
 */
public class Mail {
	
	private boolean enable = true;
	
	private String receiver;//收件人，多个以英文分号（;）分割
	
	private String subject;//主题
	
	private String content;//内容
	
	private Map<String,String> placeHolderMap;

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String, String> getPlaceHolderMap() {
		return placeHolderMap;
	}

	public void setPlaceHolderMap(Map<String, String> placeHolderMap) {
		this.placeHolderMap = placeHolderMap;
	}
}
