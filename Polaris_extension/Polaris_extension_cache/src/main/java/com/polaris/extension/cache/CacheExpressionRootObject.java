package com.polaris.extension.cache;

import java.lang.reflect.Method;
import java.util.Collection;

import org.springframework.util.Assert;


class CacheExpressionRootObject {  
	  
    private final Collection<? extends Cache> caches;  
  
    private final Method method;  
  
    private final Object[] args;  
  
    private final Object target;  
  
    private final Class<?> targetClass;  
  
  
    public CacheExpressionRootObject(  
            Collection<? extends Cache> caches, Method method, Object[] args, Object target, Class<?> targetClass) {  
  
        Assert.notNull(method, "Method is required");  
        Assert.notNull(targetClass, "targetClass is required");  
        this.method = method;  
        this.target = target;  
        this.targetClass = targetClass;  
        this.args = args;  
        this.caches = caches;  
    }  
  
  
    public Collection<? extends Cache> getCaches() {  
        return this.caches;  
    }  
  
    public Method getMethod() {  
        return this.method;  
    }  
  
    public String getMethodName() {  
        return this.method.getName();  
    }  
  
    public Object[] getArgs() {  
        return this.args;  
    }  
  
    public Object getTarget() {  
        return this.target;  
    }  
  
    public Class<?> getTargetClass() {  
        return this.targetClass;  
    }  
  
} 
