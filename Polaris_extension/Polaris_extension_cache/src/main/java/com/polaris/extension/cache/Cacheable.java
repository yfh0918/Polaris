package com.polaris.extension.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })  
@Retention(RetentionPolicy.RUNTIME) 
public @interface Cacheable {
	/** 
     * The cache key. 
     *  
     * @return 
     */  
    String key() default "";  
  
    /** 
     * The cache timeout, unit for seconds. 
     *  
     * @return 
     */  
    int timeout() default 0;  
  
    /** 
     * Whether serialize the cache object. 
     *  
     * @return 
     */  
    boolean serialize() default false; 
    
	/** 
     * The cache type. 
     *  
     * @return 
     */  
    CacheOperType type() default CacheOperType.CACHE;  
}
