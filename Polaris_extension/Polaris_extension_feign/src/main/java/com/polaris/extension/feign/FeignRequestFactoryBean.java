package com.polaris.extension.feign;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.FactoryBean;

public class FeignRequestFactoryBean implements FactoryBean<Object> {
    private Class<T> invokerInterface;
 
    public FeignRequestFactoryBean(Class<T> invokerInterface) {
        this.invokerInterface = invokerInterface;
    }
    
 
    @Override
    public Object getObject() throws Exception {
        return FeignClient.target(invokerInterface);
    }
 
    @Override
    public Class<?> getObjectType() {
        return this.invokerInterface;
    }
 
    @Override
    public boolean isSingleton() {
        return true;
    }
}
