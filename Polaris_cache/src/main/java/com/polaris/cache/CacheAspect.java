package com.polaris.cache;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;

import com.polaris.cache.serializer.ObjectSerializer;
import com.polaris.cache.util.CacheUtil;


public class CacheAspect extends CacheAspectSupport {  
	
	@SuppressWarnings("rawtypes")
	private ObjectSerializer serializer = CacheUtil.getSerializer();
	
	public static final String CACHE_AOP_METHOD = "aopmethod";
	
    @SuppressWarnings("rawtypes")
    protected ObjectSerializer getSerializer() {
		return serializer;
	}

	@SuppressWarnings("rawtypes")
	protected void setSerializer(ObjectSerializer serializer) {
		this.serializer = serializer;
	}

	protected Object cacheData(ProceedingJoinPoint joinPoint) throws Throwable {
		this.setCache(CacheFactory.getCache(CACHE_AOP_METHOD));
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();  
        Method targetMethod = AopUtils.getMostSpecificMethod(methodSignature.getMethod(), joinPoint.getTarget().getClass()); 
        Cacheable cacheable = null;
        if (targetMethod != null && targetMethod.isAnnotationPresent(Cacheable.class)) {
        	cacheable = targetMethod.getAnnotation(Cacheable.class);
            CacheOperationContext context = getOperationContext(targetMethod, joinPoint.getArgs(), joinPoint.getTarget(), joinPoint.getTarget().getClass());  
            Object key = generateKey(cacheable, context);  
      
            //普通缓存
            if (cacheable.type().equals(CacheOperType.CACHE)) {
                Object cacheObject = CacheUtil.getCacheObject(cache, key, cacheable.serialize());  
                if (null != cacheObject) {  
                    return cacheObject;  
                }  
          
                Object result = joinPoint.proceed();  
                if (null != result) {
                	CacheUtil.cacheObject(cache, key, result, cacheable.serialize(), cacheable.timeout());
                }  
                return result;  
            }
            
            //只缓存
            if (cacheable.type().equals(CacheOperType.PUT)) {
                Object result = joinPoint.proceed();  
                if (null != result) {
                	CacheUtil.cacheObject(cache, key, result, cacheable.serialize(), cacheable.timeout());
                }  
                return result;  
            }
            
            //淘汰缓存
            if (cacheable.type().equals(CacheOperType.EVICT)) {
            	Object result = joinPoint.proceed(); 
            	CacheUtil.removeCacheObject(cache, key, cacheable.serialize());
            	return result;  
            }
        }
        return joinPoint.proceed();
    }  
} 
