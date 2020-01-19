//package com.polaris.demo;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.dubbo.config.MethodConfig;
//import org.apache.dubbo.config.ServiceConfig;
//import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.polaris.demo.api.service.DemoEntryIF;
//import com.polaris.demo.core.entry.DemoEntry;
//
//@Configuration
//public class ProviderConfiguration {
//	

////    //<dubbo:protocol name="dubbo" port="20882"></dubbo:protocol>
////    @Bean
////    @Primary
////    public ProtocolConfig protocolConfig() {
////        ProtocolConfig protocolConfig = new ProtocolConfig();
////        protocolConfig.setName(ConfClient.get("dubbo.protocol.name"));
////        protocolConfig.setPort(Integer.parseInt(ConfClient.get("dubbo.protocol.port")));
////        return protocolConfig;
////    }
//    
//    /**
//    <dubbo:service interface="com.polaris.demo.api.service.DemoEntryIF" ref="demoEntry" timeout="1000" version="1.0.0">
//        <dubbo:method name="test" timeout="1000"/>
//    </dubbo:service>
//     */
////    @Bean
////    @Primary
////    public ServiceConfig<DemoEntryIF> serviceConfig(DemoEntry demoEntry){
////        ServiceConfig<DemoEntryIF> serviceConfig = new ServiceConfig<>();
////        serviceConfig.setInterface(DemoEntryIF.class);
////        serviceConfig.setRef(demoEntry);
////        serviceConfig.setVersion("1.0.0");
////        
////        //配置每一个method的信息
////        MethodConfig methodConfig1 = new MethodConfig();
////        methodConfig1.setName("test");
////        methodConfig1.setTimeout(1000);
////
////        MethodConfig methodConfig2 = new MethodConfig();
////        methodConfig2.setName("test2");
////        methodConfig2.setTimeout(1000);
////        
////        //将method的设置关联到service配置中
////        List<MethodConfig> methods = new ArrayList<>();
////        methods.add(methodConfig1);
////        methods.add(methodConfig2);
////        serviceConfig.setMethods(methods);
////        
////        //ProviderConfig
////        //MonitorConfig
////        
////        return serviceConfig;
////    }
//}
