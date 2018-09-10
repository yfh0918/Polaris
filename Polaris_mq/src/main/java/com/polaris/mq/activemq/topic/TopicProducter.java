package com.polaris.mq.activemq.topic;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Topic;

import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.StringUtil;
import com.polaris.mq.activemq.ProducterBase;
import com.polaris.mq.activemq.dto.ActiveMqDto;
import com.polaris.mq.common.dto.MqDto;

public class TopicProducter extends ProducterBase {
    private static final LogUtil logger = LogUtil.getInstance(TopicProducter.class);
    
    //队列
    Topic topic;
    
    //初始化连接
    @Override
    public void initialize(MqDto inputDto){
    	if (!(inputDto instanceof ActiveMqDto)) {
    		return;
    	}
    	ActiveMqDto dto = (ActiveMqDto)inputDto;
    	
    	//初始化参数
    	super.initialize(dto);
    	String topicName = "Polaris_Topic";
    	int deliveryMode = DeliveryMode.PERSISTENT;
        try {
        	
        	//获取参数
        	if (dto != null) {
        		
            	//topicName
               	if (StringUtil.isNotEmpty(dto.getTopicName())) {
               		topicName = dto.getTopicName();
            	}

               	//DELIVER_MODE
               	if (dto.getDeliverMode() != null) {
               		deliveryMode = dto.getDeliverMode();
            	}
        	}
         	
            //创建一个消息队列和响应的生产者
            topic = session.createTopic(topicName);
            messageProducer = session.createProducer(topic);
            messageProducer.setDeliveryMode(deliveryMode);//默认持久化
            

        } catch (JMSException e) {
        	logger.error(e);
        }
    }


}
