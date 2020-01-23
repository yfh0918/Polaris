package com.polaris.container.dubbo.config;

import java.lang.reflect.Method;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

import com.polaris.core.config.ConfClient;
import com.polaris.core.util.ReflectionUtil;

@Configuration
public class DefaultDubboConfig {
	@Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        ReflectionUtils.doWithMethods(ApplicationConfig.class, new MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				ReflectionUtil.setMethodValueForSet(method, applicationConfig, "dubbo.application.");
			}
		});
        applicationConfig.setName(ConfClient.getAppName());
        applicationConfig.setQosEnable(Boolean.parseBoolean(ConfClient.get("dubbo.application.qosEnable", "false")));
        return applicationConfig;
    }
    
    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        ReflectionUtils.doWithMethods(RegistryConfig.class, new MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				ReflectionUtil.setMethodValueForSet(method, registryConfig, "dubbo.registry.");
			}
		});
        return registryConfig;
    }
    
    @Bean
    public ProtocolConfig protocolConfig() {
    	ProtocolConfig protocolConfig = new ProtocolConfig();
        ReflectionUtils.doWithMethods(ProtocolConfig.class, new MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				ReflectionUtil.setMethodValueForSet(method, protocolConfig, "dubbo.protocol.");
			}
		});
        return protocolConfig;
    }
    
    @Bean    
    public ProviderConfig providerConfig() {
    	ProviderConfig providerConfig = new ProviderConfig();
        ReflectionUtils.doWithMethods(ProviderConfig.class, new MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				ReflectionUtil.setMethodValueForSet(method, providerConfig, "dubbo.provider.");
			}
		});
    	return providerConfig;
    }
    

}
