package com.polaris.demo.rest.controller;

import java.nio.ByteBuffer;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component; 

/**
 * websocket协议接入
 *
 * @return
 */
@Component
@ServerEndpoint("/websocket")
public class WsProtocol  {
	final static Logger logger = LoggerFactory.getLogger(WsProtocol.class);
	public static String protocolId = null;
	
    /**  
     * 打开连接时执行  
     * @param session  
     */  
    @OnOpen  
    public void start(Session session) { 
    	logger.info("open session_id:{}, parameter:{}",session.getId(),session.getRequestParameterMap());
    }  
  
    /**  
     * 接收信息时执行  
     * @param session  
     * @param msg 字符串信息  
     * @param last  
     */  
    @OnMessage  
    public void echoTextMessage(Session session, String msg, boolean last) {  
    }  
  
    /**  
     * 接收信息时执行  
     * @param session  
     * @param bb 二进制数组  
     * @param last  
     */  
    @OnMessage  
    public void echoBinaryMessage(Session session, ByteBuffer byteBuffer, boolean last) {  
    }  

      
    @OnClose  
    public void end(Session session) { 
     	logger.info("close session_id:{}",session.getId());
    }
    
    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        logger.info("session_id:{},error:{}",session.getId(),error.toString());
    }

}
