package com.polaris.mq;

import com.polaris.mq.common.Consumer;
import com.polaris.mq.common.ConsumerFactory;
import com.polaris.mq.common.Producter;
import com.polaris.mq.common.ProducterFactory;
import com.polaris.mq.kafka.dto.KafkaDto;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
//    	//active-生产者
//    	ActiveMqDto dto = new ActiveMqDto();
//    	dto.setTopic(false);
//    	dto.setAsyn(false);
//    	dto.setBrokenUrl("http://xxx");
//    	dto.setUserName("xxx");
//    	dto.setPassword("xxx");
//    	dto.setQueneName("test");
//    	dto.setDeliverMode(DeliveryMode.PERSISTENT);
//    	Producter producter = ProducterFactory.getInstance(dto);
//    	producter.sendMessage("hello1234");
//    	producter.sendMessage("daf");
//    	producter.sendMessage("helgdafadsflo1234");
//    	producter.close();
//    	
//    	//active-消费者
//    	dto = new ActiveMqDto();
//    	dto.setTopic(false);
//    	dto.setAsyn(false);
//    	dto.setBrokenUrl("http://xxx");
//    	dto.setUserName("xxx");
//    	dto.setPassword("xxx");
//    	dto.setQueneName("test");
//    	dto.setDeliverMode(DeliveryMode.PERSISTENT);
//    	Consumer consumer = ConsumerFactory.getInstance(dto);
//    	while(true) {
//    		consumer.receiveMessage();
//    	}
//    	consumer.close();
    	
    	//kafka-生产者
    	KafkaDto kdto = new KafkaDto();
    	kdto.setBrokerList("http://xxx");
    	kdto.setTopicName("teas");
    	kdto.setTopicKey("patiaon1");
    	Producter producter = ProducterFactory.getInstance(kdto);
    	producter.sendMessage("aaa");
    	producter.close();
    	
    	//kafka-消费者
    	kdto = new KafkaDto();
    	kdto.setBrokerList("http://xxx");
    	kdto.setTopicName("teas");
    	kdto.setTopicKey("patiaon1");
    	kdto.setGroupId("groupa");
    	Consumer consumer = ConsumerFactory.getInstance(kdto);
    	consumer.receiveMessage();
    }
}
