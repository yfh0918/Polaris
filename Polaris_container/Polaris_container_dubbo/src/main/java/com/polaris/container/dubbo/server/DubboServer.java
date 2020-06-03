package com.polaris.container.dubbo.server;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.polaris.container.ServerOrder;
import com.polaris.container.SpringContextServer;
import com.polaris.core.config.ConfClient;

@Order(ServerOrder.DUBBO)
public class DubboServer extends SpringContextServer{
    
    private static Logger logger = LoggerFactory.getLogger(DubboServer.class);
    
    
    /**
     * 启动服务器
     *
     * @throws Exception
     */
    public void start() throws Exception {

        //start
        super.start();
        
        //block
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("Dubbo started on port(s) " + ConfClient.get("dubbo.protocol.port"));
                    new CountDownLatch(1).await();
                } catch (Exception e) {
                    logger.error("ERROR:",e);
                }
            }
        }, "Dubbo-block").start();
    }
    
}
