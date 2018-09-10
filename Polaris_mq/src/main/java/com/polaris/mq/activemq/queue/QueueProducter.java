package com.polaris.mq.activemq.queue;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Queue;

import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.StringUtil;
import com.polaris.mq.activemq.ProducterBase;
import com.polaris.mq.activemq.dto.ActiveMqDto;
import com.polaris.mq.common.dto.MqDto;

public class QueueProducter extends ProducterBase {
    private static final LogUtil logger = LogUtil.getInstance(QueueProducter.class);
    
    //队列
    Queue queue;
    
    //初始化连接
    @Override
    public void initialize(MqDto inputDto){
    	if (!(inputDto instanceof ActiveMqDto)) {
    		return;
    	}
    	ActiveMqDto dto =(ActiveMqDto)inputDto;
    	//初始化参数
    	super.initialize(dto);
    	String queueName = "Polaris_Quene";
    	int deliveryMode = DeliveryMode.PERSISTENT;
        try {
        	
        	//获取参数
        	if (dto != null) {
        		
            	//queueName
               	if (StringUtil.isNotEmpty(dto.getQueneName())) {
               		queueName = dto.getQueneName();
            	}
               	
               	//DELIVER_MODE
               	if (dto.getDeliverMode() != null) {
               		deliveryMode = dto.getDeliverMode();
            	}
        	}
         	
            //创建一个消息队列和响应的生产者
            queue = session.createQueue(queueName);
            messageProducer = session.createProducer(queue);
            messageProducer.setDeliveryMode(deliveryMode);//默认持久化
        } catch (JMSException e) {
        	logger.error(e);
        }
    }


}
