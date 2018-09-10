package com.polaris.mq.common;

import com.polaris.mq.activemq.dto.ActiveMqDto;
import com.polaris.mq.activemq.queue.QueueProducter;
import com.polaris.mq.common.dto.MqDto;
import com.polaris.mq.kafka.dto.KafkaDto;

public class ProducterFactory {

	private ProducterFactory() {
	}
	
	//获取工厂类实例
	public static Producter getInstance(MqDto dto) {
		Producter producter = null;
		
		//activeMq
		if (dto instanceof ActiveMqDto) {
			ActiveMqDto tempDto = (ActiveMqDto)dto;
			
			//topic;
			if (tempDto.isTopic()) {
				producter = new com.polaris.mq.activemq.topic.TopicProducter();
			} else {
				//queue;
				producter = new QueueProducter();
			}
			producter.initialize(dto);
			
		//kafka
		} else if (dto instanceof KafkaDto) {
			producter = new com.polaris.mq.kafka.topic.TopicProducter();
			producter.initialize(dto);
		}
		return producter;
	}
}
