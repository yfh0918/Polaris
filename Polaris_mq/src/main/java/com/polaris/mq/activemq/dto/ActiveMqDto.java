package com.polaris.mq.activemq.dto;

import java.util.concurrent.BlockingQueue;

import com.polaris.mq.common.dto.MqDto;

public class ActiveMqDto extends MqDto{
	
	//null or true:主题 , 1:队列
	private Boolean topic;
	
	//null or true异步， false：同步(只用于消费者Consumer)
	private Boolean asyn;
	
	//阻塞队列,获取
	BlockingQueue<String> messageQueue;

	//ActiveMq 的默认用户名
    private String userName;
    
    //ActiveMq 的默认登录密码
    private String password;
    
    //ActiveMQ 的链接地址
    private String brokerUrl;
    
    //ActiveMQ 的事务方式
    private Integer ackMode;
    
    //ActiveMQ 的队列名称
    private String queneName;

    //ActiveMQ 的提交方式
    private Integer deliverMode;
    
    //设置消息预取条数
    private Integer fetchSize;
    
    //ActiveMQ 的主题名称
    private String topicName;
    
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBrokerUrl() {
		return brokerUrl;
	}

	public void setBrokenUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}

	public Integer getAckMode() {
		return ackMode;
	}

	public void setAckMode(Integer ackMode) {
		this.ackMode = ackMode;
	}

	public String getQueneName() {
		return queneName;
	}

	public void setQueneName(String queneName) {
		this.queneName = queneName;
	}

	public Integer getDeliverMode() {
		return deliverMode;
	}

	public void setDeliverMode(Integer deliverMode) {
		this.deliverMode = deliverMode;
	}

	public Integer getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(Integer fetchSize) {
		this.fetchSize = fetchSize;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public Boolean isTopic() {
		if (topic == null) {
			return true;
		}
		return topic;
	}

	public void setTopic(Boolean topic) {
		this.topic = topic;
	}

	public Boolean isAsyn() {
		if (asyn == null) {
			return true;
		}
		return asyn;
	}

	public void setAsyn(Boolean asyn) {
		this.asyn = asyn;
	}

	public BlockingQueue<String> getMessageQueue() {
		return messageQueue;
	}

	public void setMessageQueue(BlockingQueue<String> messageQueue) {
		this.messageQueue = messageQueue;
	}
}
