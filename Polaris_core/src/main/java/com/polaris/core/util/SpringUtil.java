package com.polaris.core.util;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * Deprecated
 * {@link SpringContextHealper}
 */
@Deprecated
public class SpringUtil {
    
    @Deprecated
    public static ConfigurableApplicationContext getApplicationContext() {
        return SpringContextHealper.getApplicationContext();
    }
    
    @Deprecated
    public static ConfigurableApplicationContext createApplicationContext(Class<?>... clazz) {
        return SpringContextHealper.createApplicationContext(clazz);
    }
    
    @Deprecated
    public static Object getBean(String serviceName){
        return SpringContextHealper.getBean(serviceName);
    }

    @Deprecated
    public static <T> T getBean(Class<T> requiredType){
        return SpringContextHealper.getBean(requiredType);
    }
    
    @Deprecated
    public static void setApplicationContext(ConfigurableApplicationContext inputContext){
        SpringContextHealper.setApplicationContext(inputContext);
    }
}
