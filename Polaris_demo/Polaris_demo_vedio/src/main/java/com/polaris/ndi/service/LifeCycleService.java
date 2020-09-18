package com.polaris.ndi.service;

import javax.websocket.Session;

public interface LifeCycleService {
    
    public enum VedioType {
        IMAGE,
        STREAM
    }
    
    public LifeCycleService addSession(Session session);
    
    public LifeCycleService removeSession(Session session);
    
    public LifeCycleService start(String streamLocation) throws Exception;
    
    public LifeCycleService stop();
    
    public boolean isRunning();
    
    public LifeCycleService setType(String type);
}
