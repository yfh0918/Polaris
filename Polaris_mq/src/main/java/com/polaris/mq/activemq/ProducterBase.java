package com.polaris.mq.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.StringUtil;
import com.polaris.mq.activemq.dto.ActiveMqDto;
import com.polaris.mq.common.Producter;
import com.polaris.mq.common.dto.MqDto;

public abstract class ProducterBase implements Producter{
    private static final LogUtil logger = LogUtil.getInstance(ProducterBase.class);
    
    //链接工厂
    protected ConnectionFactory connectionFactory;
    
    //链接对象
    protected Connection connection;
    
    //事务管理
    protected Session session;
    
    //消息生产者
    protected MessageProducer messageProducer;
    
    //初始化连接
    @Override
    public void initialize(MqDto inputDto){
    	if (!(inputDto instanceof ActiveMqDto)) {
    		return;
    	}
    	//初始化参数
    	ActiveMqDto dto = (ActiveMqDto)inputDto;
    	
    	//初始化参数
    	String userName = ActiveMQConnection.DEFAULT_USER;
    	String password = ActiveMQConnection.DEFAULT_PASSWORD;
    	String brokerUrl = ActiveMQConnection.DEFAULT_BROKER_URL; 
    	int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
        try {
        	
        	//获取参数
        	if (dto != null) {
        		
        		//用户名
               	if (StringUtil.isNotEmpty(dto.getUserName())) {
            		userName = dto.getUserName();
            	}
               	
            	//密码
               	if (StringUtil.isNotEmpty(dto.getPassword())) {
               		password = dto.getPassword();
            	}
            	
            	//URL
               	if (StringUtil.isNotEmpty(dto.getBrokerUrl())) {
               		brokerUrl = dto.getBrokerUrl();
            	}
               	
            	//ACK_MODE
               	if (dto.getAckMode() != null) {
               		acknowledgeMode = dto.getAckMode();
            	}
        	}
         	
            //创建一个链接工厂
            connectionFactory = new ActiveMQConnectionFactory(userName,password,brokerUrl);
            
            //从工厂中创建一个链接
            connection  = connectionFactory.createConnection();
            
            //开启链接
            connection.start();
            
            //创建一个事务
            if (Session.SESSION_TRANSACTED == acknowledgeMode) {
                session = connection.createSession(true,Session.SESSION_TRANSACTED);
            } else {
                session = connection.createSession(false,acknowledgeMode);
            }
            

        } catch (JMSException e) {
        	logger.error(e);
        }
    }

    //发送信息给服务器
    @Override
    public void sendMessage(String message){
        try {
        	if (StringUtil.isEmpty(message)) {
        		return;
        	}
            //发送消息
        	TextMessage textMsg = session.createTextMessage(message);
            messageProducer.send(textMsg);
            
            //是事务提交方式
            if (Session.SESSION_TRANSACTED == session.getAcknowledgeMode()) {
                session.commit();
            }
        } catch (JMSException e) {
        	e.printStackTrace();
        	logger.error(e);
        } 
    }
    
    //发送信息给服务器
    @Override
    public void sendMessage(String[] messages){
        try {
        	if (messages == null || messages.length == 0) {
        		return;
        	}
            //发送消息
        	for (String message : messages) {
        		if (StringUtil.isNotEmpty(message)) {
            		TextMessage textMsg = session.createTextMessage(message);
                    messageProducer.send(textMsg);
        		}
        	}
            
            //是事务提交方式
            if (Session.SESSION_TRANSACTED == session.getAcknowledgeMode()) {
                session.commit();
            }
        } catch (JMSException e) {
        	e.printStackTrace();
        	logger.error(e);
        } 
    }
    
    //关闭
    @Override
    public void close(){
    	
    	//关闭生产者
    	if (messageProducer != null) {
    		try {
				messageProducer.close();
			} catch (JMSException e) {
				logger.error(e);
			}
    		messageProducer = null;
    	}
    	
    	//关闭session
    	if (session != null) {
    		try {
    			session.close();
    		} catch (JMSException e) {
    			logger.error(e);
    		}
    		session = null;
    	}
    	
    	//关闭连接
    	if (connection != null) {
    		try {
				connection.close();
			} catch (JMSException e) {
				logger.error(e);
			}
    		connection = null;
    	}
    }
}
