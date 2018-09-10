package com.polaris.mq.common;

import com.polaris.mq.activemq.dto.ActiveMqDto;
import com.polaris.mq.activemq.queue.QueueConsumerAsyn;
import com.polaris.mq.activemq.queue.QueueConsumerSyn;
import com.polaris.mq.activemq.topic.TopicConsumerAsyn;
import com.polaris.mq.activemq.topic.TopicConsumerSyn;
import com.polaris.mq.common.dto.MqDto;
import com.polaris.mq.kafka.dto.KafkaDto;

public class ConsumerFactory {
	private ConsumerFactory() {
	}
	
	//获取工厂类实例
	public static Consumer getInstance(MqDto dto) {
		Consumer consumer = null;
		
		//activeMq
		if (dto instanceof ActiveMqDto) {
			ActiveMqDto tempDto = (ActiveMqDto)dto;
			
			//topic;
			if (tempDto.isTopic()) {
				if (tempDto.isAsyn()) {
					consumer = new TopicConsumerAsyn();
				} else {
					consumer = new TopicConsumerSyn();
				}
				
			//queue
			} else {
				if (tempDto.isAsyn()) {
					consumer = new QueueConsumerAsyn();
				} else {
					consumer = new QueueConsumerSyn();
				}
			}
			consumer.initialize(dto);
			
		//kafka
		} else if (dto instanceof KafkaDto) {
			consumer = new com.polaris.mq.kafka.topic.TopicConsumer();
			consumer.initialize(dto);
		}
		return consumer;
	}
}
