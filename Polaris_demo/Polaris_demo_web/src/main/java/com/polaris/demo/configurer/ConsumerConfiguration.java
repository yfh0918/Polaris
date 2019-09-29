package com.polaris.demo.configurer;

import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.MethodConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.polaris.core.config.ConfClient;
import com.polaris.demo.api.service.DemoEntryIF;

@Configuration
@EnableDubbo(scanBasePackages = "com.polaris.demo.rest.controller")
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
    

    @Bean
    public DemoEntryIF demoEntryIF(ApplicationConfig applicationConfig, RegistryConfig registryConfig){
    	ReferenceConfig<DemoEntryIF> referenceConfig = new ReferenceConfig<>();
    	
    	referenceConfig.setApplication(applicationConfig);
        referenceConfig.setRegistry(registryConfig);
        
    	referenceConfig.setInterface(DemoEntryIF.class);
    	referenceConfig.setVersion("1.0.0");
    	referenceConfig.setUrl("dubbo://192.168.1.13:20880");
        
        //配置每一个method的信息
        MethodConfig methodConfig1 = new MethodConfig();
        methodConfig1.setName("test");
        methodConfig1.setTimeout(1000);

        MethodConfig methodConfig2 = new MethodConfig();
        methodConfig2.setName("test2");
        methodConfig2.setTimeout(1000);
        
        //将method的设置关联到service配置中
        List<MethodConfig> methods = new ArrayList<>();
        methods.add(methodConfig1);
        methods.add(methodConfig2);
        referenceConfig.setMethods(methods);
        
        //ProviderConfig
        //MonitorConfig
        
        return referenceConfig.get();
    }
    
}
