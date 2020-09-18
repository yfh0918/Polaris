package com.polaris.ndi.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.thread.ThreadPoolBuilder;

public  abstract class LifeCycleServiceAbstract implements LifeCycleService  {
    
    private static Logger logger = LoggerFactory.getLogger(LifeCycleServiceAbstract.class);
    
    protected volatile boolean running = false;
    
    protected volatile Set<Session> sessions = new HashSet<>();
    
    protected volatile VedioType type = VedioType.IMAGE;

    protected static ThreadPoolExecutor newSingleThread(String name) {
        ThreadPoolExecutor threadPool = ThreadPoolBuilder.newBuilder()
                .poolName(name)
                .coreThreads(1)
                .maximumThreads(1)
                .keepAliveSeconds(10l)
                .workQueue(new LinkedBlockingDeque<Runnable>(10000))
                .build();
        return threadPool;
    }
    
    @Override
    public LifeCycleService stop() {
        running = false;
        return this;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public LifeCycleService addSession(Session session) {
        sessions.add(session);
        return this;
    }
    @Override
    public LifeCycleService removeSession(Session session) {
        sessions.remove(session);
        return this;
    }
    
    /**
     * 数据输出
     */
    protected void output(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return;
        }
        for (Session session : sessions) {
            if (session != null && session.isOpen()) { 
                try {
                    session.getBasicRemote().sendBinary(byteBuffer);
                } catch (IOException e) {
                    logger.error("error:{}",e);
                }
            } 
        }
        byteBuffer.clear();
    }

    public LifeCycleService setType(String type) {
        if (type.equalsIgnoreCase(VedioType.STREAM.name())) {
            this.type = VedioType.STREAM;
        } else {
            this.type = VedioType.IMAGE;
        }
        return this;
    }
}
