package com.polaris.dubbo.config;

import java.lang.reflect.Field;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.polaris.core.config.ConfClient;
import com.polaris.core.util.StringUtil;

@Configuration
public class DefaultDubboConfig {
	private static Logger logger = LoggerFactory.getLogger(DefaultDubboConfig.class);
	@Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(ConfClient.getAppName());
        ReflectionUtils.doWithFields(ApplicationConfig.class, new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				String fieldName = "dubbo.application." + field.getName();
				setFieldValue(field,applicationConfig,fieldName);
			}
    		
    	});
        return applicationConfig;
    }
    
    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        ReflectionUtils.doWithFields(RegistryConfig.class, new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				String fieldName = "dubbo.registry." + field.getName();
				setFieldValue(field,registryConfig,fieldName);
			}
    		
    	});
        return registryConfig;
    }
    
    @Bean
    public ProtocolConfig protocolConfig() {
    	ProtocolConfig protocolConfig = new ProtocolConfig();
    	ReflectionUtils.doWithFields(ProtocolConfig.class, new FieldCallback() {

			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				String fieldName = "dubbo.protocol." + field.getName();
				setFieldValue(field,protocolConfig,fieldName);
			}
    		
    	});
        return protocolConfig;
    }
    
    @Bean    
    public ProviderConfig providerConfig() {
    	ProviderConfig providerConfig = new ProviderConfig();
     	ReflectionUtils.doWithFields(ProviderConfig.class, new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				String fieldName = "dubbo.provider." + field.getName();
				setFieldValue(field,providerConfig,fieldName);
			}
    		
    	});
    	return providerConfig;
    }
    
    private void setFieldValue(Field field, Object obj, String fieldName) {
    	String fieldValue = ConfClient.get(fieldName);
		if (StringUtil.isEmpty(fieldValue)) {
			return;
		}
		try {
	    	if (field.getType() == String.class) {
	    		field.setAccessible(true);
				field.set(obj, fieldValue);
	    	} else if (field.getType() == Integer.class) {
	    		field.setAccessible(true);
				field.set(obj, Integer.parseInt(fieldValue));
	    	} else if (field.getType() == Boolean.class) {
	    		field.setAccessible(true);
				field.set(obj, Boolean.parseBoolean(fieldValue));
	    	} else if (field.getType() == Long.class) {
	    		field.setAccessible(true);
				field.set(obj, Long.parseLong(fieldValue));
	    	}
		} catch (Exception e) {
			logger.error("ERROR:",e);
		} 

    }
}
