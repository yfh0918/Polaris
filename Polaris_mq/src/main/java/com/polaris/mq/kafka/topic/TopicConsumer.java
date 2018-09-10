package com.polaris.mq.kafka.topic;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.polaris.comm.util.StringUtil;
import com.polaris.mq.common.Consumer;
import com.polaris.mq.common.dto.MqDto;
import com.polaris.mq.kafka.dto.KafkaDto;

public class TopicConsumer implements Consumer{
    private String TOPIC_NAME = "Polaris_Kafka_Topic";
    private String KEY_NAME = "Polaris_Kafka_Key";
    private String GROUP_ID = "Polaris_Kafka_Group";
	private KafkaConsumer<String, String> kafkaConsumer;
	
	//初始化连接
    public void initialize(MqDto inputDto){
    	
    	//获取参数
    	if (!(inputDto instanceof KafkaDto)) {
    		return;
    	}
    	KafkaDto dto = (KafkaDto)inputDto;
    	if (StringUtil.isNotEmpty(dto.getTopicName())) {
    		TOPIC_NAME = dto.getTopicName();
    	}
    	if (StringUtil.isNotEmpty(dto.getTopicKey())) {
    		KEY_NAME = dto.getTopicKey();
    	}
    	if (StringUtil.isNotEmpty(dto.getGroupId())) {
    		GROUP_ID = dto.getGroupId();
    	}
    	
    	//初始化参数
    	Properties props = new Properties();  
    	props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, dto.getBrokerList());//格式：host1:port1,host2:port2,....
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);//a batch size of zero will disable batching entirely
        props.put( ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(Collections.singletonList(TOPIC_NAME));
    }
    
    //同步阻塞接受消息（设定timeout的时间）
    public String[] receiveMessage(long timeout){
    	ConsumerRecords<String,String> records = kafkaConsumer.poll(timeout);
    	if (records == null || records.count() == 0) {
    		return null;
    	}
    	String[] rtnArray = new String[records.count()];
    	int count = 0;
    	for (ConsumerRecord<String, String> record : records) {
    		if (KEY_NAME.equals(record.key())) {
        		rtnArray[count] = record.value();
        		count++;
    		}
    	}
    	return rtnArray;
    }

    //同步阻塞接受消息
    public String[] receiveMessage(){
    	return receiveMessage(Long.MAX_VALUE);
    }
    
    //关闭连接
    public void close(){
    	if (kafkaConsumer != null) {
    		kafkaConsumer.close();
    	}
    }
}
