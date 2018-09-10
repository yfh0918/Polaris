package com.polaris.mq.common;

import com.polaris.mq.common.dto.MqDto;

public interface Producter {
	
	//初始化连接
    void initialize(MqDto dto);
    
    //发送数据
    void sendMessage(String message);

    //发送数据
    void sendMessage(String[] messages);
    
    //关闭连接
    void close();
}
