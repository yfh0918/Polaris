package com.polaris.core.pojo;

import java.util.List;
import java.util.Map;
/**
 * Mail
 *
 */
public class Mail {
	
	private String key;//邮件对象关键字，可以为空
	
	private boolean enable = true;
	
	private String receiver;//收件人，多个以英文分号（;）分割
	
	private String subject;//主题
	
	private String content;//内容
	
	private List<String> attachFilePathList;
	
	private Map<String,String> placeHolderMap;



	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

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

	public List<String> getAttachFilePathList() {
		return attachFilePathList;
	}

	public void setAttachFilePathList(List<String> attachFilePathList) {
		this.attachFilePathList = attachFilePathList;
	}
}
