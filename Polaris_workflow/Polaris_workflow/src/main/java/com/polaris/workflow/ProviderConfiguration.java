package com.polaris.workflow;

import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.config.MethodConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.polaris.container.dubbo.annotation.EnablePolarisDubbo;
import com.polaris.workflow.api.service.WorkflowService;

@EnablePolarisDubbo
@Configuration
public class ProviderConfiguration {
	
    /**
    <dubbo:service interface="com.polaris.demo.api.service.DemoEntryIF" ref="demoEntry" timeout="1000" version="1.0.0">
        <dubbo:method name="test" timeout="1000"/>
    </dubbo:service>
     */
    @Bean
    public ServiceConfig<WorkflowService> serviceConfig(ApplicationDubbo workflowService){
        ServiceConfig<WorkflowService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setInterface(WorkflowService.class);
        serviceConfig.setRef(workflowService);
        
        //配置每一个method的信息
        MethodConfig methodConfig1 = new MethodConfig();
        methodConfig1.setName("deployDiagram");
        methodConfig1.setTimeout(1000);

        MethodConfig methodConfig2 = new MethodConfig();
        methodConfig2.setName("startWorkflow");
        methodConfig2.setTimeout(1000);
        
        //将method的设置关联到service配置中
        List<MethodConfig> methods = new ArrayList<>();
        methods.add(methodConfig1);
        methods.add(methodConfig2);
        serviceConfig.setMethods(methods);
        
        //ProviderConfig
        //MonitorConfig
        
        return serviceConfig;
    }
}
