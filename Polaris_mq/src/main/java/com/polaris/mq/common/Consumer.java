package com.polaris.mq.common;

import com.polaris.mq.common.dto.MqDto;

public interface Consumer {
	
	//初始化连接
    void initialize(MqDto dto);
    
    //同步阻塞接受消息（设定timeout的时间）
    String[] receiveMessage(long timeout);

    //同步阻塞接受消息
    String[] receiveMessage();
    
    //关闭连接
    void close();
}
