package com.polaris.ndi.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.ndi.service.LifeCycleServiceFactory;

@ServerEndpoint("/ws/vedio")
public class VedioGrabberHandler {
    
    private static Logger logger = LoggerFactory.getLogger(VedioGrabberHandler.class);
    
    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("message from server " + message);
    }
    
    @OnOpen
    public void onOpen(Session session){
        logger.info("session is created " + session);
        LifeCycleServiceFactory.get().addSession(session);
    }
    
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session){
        logger.info("ndi websocket session is closed ");
        LifeCycleServiceFactory.get().removeSession(session);
    }
    
    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        logger.error("ndi websocket error:{}",error);
        LifeCycleServiceFactory.get().removeSession(session);
    }
}
