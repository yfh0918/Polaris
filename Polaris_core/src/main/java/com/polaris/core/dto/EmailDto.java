package com.polaris.core.dto;

import java.util.Map;
/**
 * EmailDto
 *
 */
public class EmailDto {
	
	private String receiver;//收件人，多个以英文分号（;）分割
	
	private String subject;//主题
	
	private String content;//内容
	
	private Map<String,String> placeHolderMap;

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
