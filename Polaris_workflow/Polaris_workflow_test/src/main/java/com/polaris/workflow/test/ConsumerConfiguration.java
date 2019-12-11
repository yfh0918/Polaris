package com.polaris.workflow.test;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.polaris.core.config.ConfClient;

@Configuration
@EnableDubbo(scanBasePackages = "com.polaris")
public class ConsumerConfiguration {
	
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(ConfClient.getAppName());
        applicationConfig.setQosEnable(false);
        return applicationConfig;
    }
    
    //<dubbo:registry protocol="zookeeper" address="127.0.0.1:2181"></dubbo:registry>
    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol(ConfClient.get("dubbo.registry.protocol"));
        registryConfig.setAddress(ConfClient.get("dubbo.registry.address"));
        return registryConfig;
    }
    
    /**
    <dubbo:consumer check="false" />
     */
    @Bean
    public ConsumerConfig consumerConfig(){
    	ConsumerConfig consumerConfig = new ConsumerConfig();
    	consumerConfig.setCheck(false);
        return consumerConfig;
    }
}
