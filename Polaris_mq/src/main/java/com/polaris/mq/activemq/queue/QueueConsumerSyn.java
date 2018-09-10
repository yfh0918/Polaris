package com.polaris.mq.activemq.queue;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import com.polaris.comm.util.LogUtil;

public class QueueConsumerSyn extends QueueConsumerBase {
    private static final LogUtil logger = LogUtil.getInstance(QueueConsumerSyn.class);
    

    //阻塞接受消息
    @Override
    public String[] receiveMessage(){
        try {
        	TextMessage textMessage = (TextMessage) messageConsumer.receive();
            if(textMessage != null){
            	return new String[]{textMessage.getText()};
            }
        } catch (JMSException e) {
        	logger.error(e);
        } 
        return null;
    }
    
    //阻塞接受消息（设定timeout的时间）
    @Override
    public String[] receiveMessage(long timeout){
        try {
        	TextMessage textMessage;
        	if (timeout == 0) {
        		textMessage = (TextMessage) messageConsumer.receiveNoWait();
        	} else {
        		textMessage = (TextMessage) messageConsumer.receive(timeout);
        	}
            if(textMessage != null){
            	return new String[]{textMessage.getText()};
            }
        } catch (JMSException e) {
        	logger.error(e);
        } 
        return null;
    }
    

}
