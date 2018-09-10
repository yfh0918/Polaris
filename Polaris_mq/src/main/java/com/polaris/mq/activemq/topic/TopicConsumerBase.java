package com.polaris.mq.activemq.topic;

import javax.jms.JMSException;
import javax.jms.Topic;

import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.StringUtil;
import com.polaris.mq.activemq.ConsumerBase;
import com.polaris.mq.activemq.dto.ActiveMqDto;
import com.polaris.mq.common.dto.MqDto;

public abstract class TopicConsumerBase extends ConsumerBase{
	
	private static final LogUtil logger = LogUtil.getInstance(TopicConsumerBase.class);
	
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
    	int prefetchSize = 1000;//消费者客户端的内存里缓存的消息数量
        try {
        	
        	//获取参数
        	if (dto != null) {

            	//topicName
               	if (StringUtil.isNotEmpty(dto.getTopicName())) {
               		topicName = dto.getTopicName();
            	}
               	
               	//FETCH_SIZE
               	if (dto.getFetchSize() != null) {
               		prefetchSize = dto.getFetchSize();
            	}

        	}
            
            //创建一个消息队列和响应的生产者
        	topic = session.createTopic(topicName+"?consumer.prefetchSize="+prefetchSize);
            messageConsumer = session.createConsumer(topic);
            
        } catch (JMSException e) {
        	logger.error(e);
        }
    }
    

}
