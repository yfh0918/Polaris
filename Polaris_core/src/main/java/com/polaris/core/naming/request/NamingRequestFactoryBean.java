package com.polaris.core.naming.request;

import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class NamingRequestFactoryBean implements FactoryBean<Object>, InitializingBean {
    private static final ServiceLoader<NamingRequestHandler> handlerLoader = ServiceLoader.load(NamingRequestHandler.class);
    private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
    private static NamingRequestHandler handler;
    private Class<T> invokerInterface;
 
    public static boolean exist() {
        if (initialized.compareAndSet(false, true)) {
            if (handler == null) {
                for (NamingRequestHandler namingRequestHandler : handlerLoader) {
                    handler = namingRequestHandler;
                }
            }
        }
        if (handler == null) {
            return false;
        }
        return true;
    }
    
    public NamingRequestFactoryBean(Class<T> invokerInterface) {
        this.invokerInterface = invokerInterface;
    }
    
 
    @Override
    public Object getObject() throws Exception {
        return handler.invoke(invokerInterface);
    }
 
    @Override
    public Class<?> getObjectType() {
        return this.invokerInterface;
    }
 
    @Override
    public boolean isSingleton() {
        return true;
    }
 
    @Override
    public void afterPropertiesSet() throws Exception {
    }

}
