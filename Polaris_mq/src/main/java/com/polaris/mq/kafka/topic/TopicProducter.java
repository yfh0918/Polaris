package com.polaris.mq.kafka.topic;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.StringUtil;
import com.polaris.mq.common.Producter;
import com.polaris.mq.common.dto.MqDto;
import com.polaris.mq.kafka.dto.KafkaDto;

public class TopicProducter implements Producter{
    private static final LogUtil logger = LogUtil.getInstance(TopicProducter.class);
    private String TOPIC_NAME = "Polaris_Kafka_Topic";
    private String KEY_NAME = "Polaris_Kafka_Key";
    
    private KafkaProducer<String, String> kafkaProducer;
    
    //初始化连接
    @Override
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
    	//初始化参数
    	Properties props = new Properties();  
    	props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, dto.getBrokerList());//格式：host1:port1,host2:port2,....
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 0);//a batch size of zero will disable batching entirely
        props.put(ProducerConfig.LINGER_MS_CONFIG, 0);//send message without delay
        if (StringUtil.isEmpty(dto.getAckMode())) {
            props.put(ProducerConfig.ACKS_CONFIG, "1");
        } else {
        	props.put(ProducerConfig.ACKS_CONFIG, dto.getAckMode());
        }
        props.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        kafkaProducer = new KafkaProducer<>(props);
    }

    //发送消息
    @Override
    public void sendMessage(String message){
        try {
        	if (StringUtil.isEmpty(message)) {
        		return;
        	}
        	ProducerRecord<String, String> messageR = new ProducerRecord<>(TOPIC_NAME, KEY_NAME, message);
			this.kafkaProducer.send(messageR).get();
		} catch (InterruptedException | ExecutionException e) {
			logger.error(e);
		}
    }
    
    //发送消息
    @Override
    public void sendMessage(String[] messages){
    	if (messages == null || messages.length == 0) {
    		return;
    	}
    	for (String message : messages) {
    		sendMessage(message);
    	}
    }

    //关闭
    @Override
    public void close(){
    	if (kafkaProducer != null) {
        	kafkaProducer.close();
    	}
    }
}
