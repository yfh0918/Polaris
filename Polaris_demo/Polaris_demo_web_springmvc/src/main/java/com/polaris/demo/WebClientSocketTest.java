package com.polaris.demo;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint
public class WebClientSocketTest {
	private String deviceId;

    private static Session session;

    public WebClientSocketTest () {
        
    }

    public WebClientSocketTest (String deviceId) {
        this.deviceId = deviceId;
    }

    protected boolean start() {
        WebSocketContainer Container = ContainerProvider.getWebSocketContainer();
        String uri = "ws://127.0.0.1:8081/banyanNDI/ws/ndi";
        System.out.println("Connecting to " + uri);
        try {
        	session = Container.connectToServer(WebClientSocketTest.class, URI.create(uri));
            System.out.println("session: " + session);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("message from server " + message);
        
    }
    
    @OnOpen
    public void onOpen(Session session){
    	System.out.println("session is created " + session);
    }
    
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session){
    	System.out.println("session is closed ");
    }
    
    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("发生错误");
    }
    
    public void sendMessage(String message) {
    	try {
			session.getBasicRemote().sendText("hello world");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void sendMessage(ByteBuffer bb) {
    	try {
    		System.out.println("ByteBuffer sended ...");
    		
			session.getBasicRemote().sendBinary(bb);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void close() {
    	try {
			session.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    

    public static void main(String[] args) throws IOException {
//        for (int i = 1; i< 100; i++) {
//        	WebClientSocketTest wSocketTest = new WebClientSocketTest(String.valueOf(i));
//            if (!wSocketTest.start()) {
//                System.out.println("测试结束！");
//                break;
//            } 
//        }
        for (int i = 1; i< 20; i++) {
            WebClientSocketTest wSocketTest = new WebClientSocketTest("1");
            wSocketTest.start();
        }
    	
//        wSocketTest.sendMessage(" first hello");
        
//    	try {
//        	byte[] bytes = FileUtil.File2byte("C:\\vedio\\file_example_WAV_10MG.wav");
//	        wSocketTest.sendMessage(ByteBuffer.wrap(bytes));
//	        wSocketTest.close();
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}

    	
        
    }
}
