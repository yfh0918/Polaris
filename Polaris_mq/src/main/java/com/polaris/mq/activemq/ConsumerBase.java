package com.polaris.mq.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.StringUtil;
import com.polaris.mq.activemq.dto.ActiveMqDto;
import com.polaris.mq.common.Consumer;
import com.polaris.mq.common.dto.MqDto;

public abstract class ConsumerBase implements Consumer{
	
	private static final LogUtil logger = LogUtil.getInstance(ConsumerBase.class);
	
    //链接工厂
    protected ConnectionFactory connectionFactory;
    
    //链接对象
    protected Connection connection;
    
    //事务管理
    protected Session session;
    
    //消息消费者
    protected MessageConsumer messageConsumer;
    
    //初始化连接
    @Override
    public void initialize(MqDto inputDto){
    	if (!(inputDto instanceof ActiveMqDto)) {
    		return;
    	}
    	//初始化参数
    	ActiveMqDto dto = (ActiveMqDto)inputDto;
    	String userName = ActiveMQConnection.DEFAULT_USER;
    	String password = ActiveMQConnection.DEFAULT_PASSWORD;
    	String brokenUrl = ActiveMQConnection.DEFAULT_BROKER_URL; 
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
               		brokenUrl = dto.getBrokerUrl();
            	}
               	
            	//ACK_MODE
               	if (dto.getAckMode() != null) {
               		acknowledgeMode = dto.getAckMode();
            	}
               	
        	}
         	
            //创建一个链接工厂
            connectionFactory = new ActiveMQConnectionFactory(userName,password,brokenUrl);
            
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
    
    //关闭
    @Override
    public void close(){
    	
    	//关闭生产者
    	if (messageConsumer != null) {
    		try {
    			messageConsumer.close();
			} catch (JMSException e) {
				logger.error(e);
			}
    		messageConsumer = null;
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
