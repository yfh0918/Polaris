package com.polaris.core.pojo;

import java.util.Map;
import java.util.Properties;
/**
 * Mail
 *
 */
public class Mail {
	
	/*
	 *  邮件对象关键字，可以为空
    */
	private String key;
	
	/*
	 *  是否发送邮件true发送，false不发送
    */
	private boolean enable = true;
	
	/*
	 *  收件人，多个以英文分号（;）分割
    */
	private String receiver;
	
	/*
	 *  主题
    */
	private String subject;
	
	/*
	 *  content中xxx{key1},yyyy{key2}
    */
	private String content;
	
	/*
	 *  附件列表
    */
	private String[] attachFilePaths;
	
	/*
	 *  content中{key} 对应的 value
    */
	private Map<String,String> placeHolderMap;
	
	/*
	 *  设置邮件系统参数
		Properties props = new Properties();
        MailSSLSocketFactory sslFactory = new MailSSLSocketFactory();
        sslFactory.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.socketFactory", sslFactory);
	    props.setProperty("mail.smtp.ssl.enable", true);
	    props.setProperty("mail.transport.protocol", smtp);
	    props.setProperty("mail.smtp.auth", true);
	    props.setProperty("mail.smtp.host", smtp.qq.com);
	    props.setProperty("mail.smtp.port", 465);
	    props.setProperty("mail.sender", xxxx@qq.com);
	    props.setProperty("mail.password", adfadsfadf);
    */
	private Properties properties;//邮件的系统信息


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

	public String[] getAttachFilePaths() {
		return attachFilePaths;
	}

	public void setAttachFilePaths(String... attachFilePaths) {
		this.attachFilePaths = attachFilePaths;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
