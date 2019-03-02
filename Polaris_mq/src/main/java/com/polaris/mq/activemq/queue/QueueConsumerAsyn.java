package com.polaris.mq.activemq.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.StringUtil;
import com.polaris.mq.activemq.dto.ActiveMqDto;
import com.polaris.mq.common.dto.MqDto;

public class QueueConsumerAsyn extends QueueConsumerBase implements MessageListener{

	private static final LogUtil logger = LogUtil.getInstance(QueueConsumerAsyn.class);
	
	//阻塞队列,获取
	BlockingQueue<String> queue;

    //初始化连接
	@Override
    public void initialize(MqDto inputDto){
    	if (!(inputDto instanceof ActiveMqDto)) {
    		return;
    	}
    	ActiveMqDto dto = (ActiveMqDto)inputDto;
    	//初始化
    	super.initialize(dto);
    	
    	//初始化获取消息的阻塞队列
    	if (dto.getMessageQueue() == null) {
    		queue = new LinkedBlockingQueue<>();
    		dto.setMessageQueue(queue);
    		
    	} else {
    		queue = dto.getMessageQueue();
    	}
    	
    	//开始监听
    	try {
        	messageConsumer.setMessageListener(this);   
        } catch (JMSException e) {
        	logger.error(e);
        } 
    }
	
	//接受消息
    @Override
    public void onMessage(Message arg0) {
    	if(arg0 instanceof TextMessage) { 
    		try {
                TextMessage txtMsg = (TextMessage) arg0; 
                queue.put(txtMsg.getText());
    		} catch (Exception ex) {
    			logger.error(ex);
    		}
        }
    }
    
    //阻塞接受消息
    @Override
    public String[] receiveMessage(){
    	try {
			return new String[]{queue.take()};
		} catch (InterruptedException e) {
			logger.error(e);
			Thread.currentThread().interrupt();
		}
		return null;
    }
    
    //阻塞接受消息,最大等待时间
    @Override
    public String[] receiveMessage(long timeout){
    	try {
    		String result;
    		if (timeout == 0) {
    			result = queue.poll();
    		} else {
    			result = queue.poll(timeout,TimeUnit.MILLISECONDS);
    		}
    		if (StringUtil.isNotEmpty(result)) {
    			return new String[]{result};
    		}
		} catch (InterruptedException e) {
			logger.error(e);
			Thread.currentThread().interrupt();
		}
		return null;
    }
    
}
