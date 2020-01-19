package com.polaris.dubbo.config;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.polaris.core.config.ConfClient;

@Configuration
public class DefaultDubboConfig {
	
	@Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(ConfClient.getAppName());
        applicationConfig.setQosEnable(false);
        return applicationConfig;
    }
    
    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol(ConfClient.get("dubbo.registry.protocol"));
        registryConfig.setAddress(ConfClient.get("dubbo.registry.address"));
        return registryConfig;
    }
    
    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName(ConfClient.get("dubbo.protocol.name"));
        protocolConfig.setPort(Integer.parseInt(ConfClient.get("dubbo.protocol.port")));
        return protocolConfig;
    }
}
