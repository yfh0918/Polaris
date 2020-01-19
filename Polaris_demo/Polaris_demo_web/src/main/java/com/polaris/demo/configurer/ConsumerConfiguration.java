//package com.polaris.demo.configurer;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.dubbo.config.ApplicationConfig;
//import org.apache.dubbo.config.MethodConfig;
//import org.apache.dubbo.config.ReferenceConfig;
//import org.apache.dubbo.config.RegistryConfig;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.polaris.demo.api.service.DemoEntryIF;
//
//@Configuration
//public class ConsumerConfiguration {

//    @Bean
//    @Primary
//    public DemoEntryIF demoEntryIF(ApplicationConfig applicationConfig, RegistryConfig registryConfig){
//    	ReferenceConfig<DemoEntryIF> referenceConfig = new ReferenceConfig<>();
//    	
//    	referenceConfig.setApplication(applicationConfig);
//        referenceConfig.setRegistry(registryConfig);
//        
//    	referenceConfig.setInterface(DemoEntryIF.class);
//    	referenceConfig.setVersion("1.0.0");
//    	referenceConfig.setUrl("dubbo://192.168.1.13:20880");
//        
//        //配置每一个method的信息
//        MethodConfig methodConfig1 = new MethodConfig();
//        methodConfig1.setName("test");
//        methodConfig1.setTimeout(1000);
//
//        MethodConfig methodConfig2 = new MethodConfig();
//        methodConfig2.setName("test2");
//        methodConfig2.setTimeout(1000);
//        
//        //将method的设置关联到service配置中
//        List<MethodConfig> methods = new ArrayList<>();
//        methods.add(methodConfig1);
//        methods.add(methodConfig2);
//        referenceConfig.setMethods(methods);
//        
//        //ProviderConfig
//        //MonitorConfig
//        
//        return referenceConfig.get();
//    }
//    
//}
