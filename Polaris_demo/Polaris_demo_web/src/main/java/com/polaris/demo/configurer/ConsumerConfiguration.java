package com.polaris.demo.configurer;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableDubbo(scanBasePackages = "com.polaris.demo.rest.controller")
@PropertySource("classpath:/config/application.properties")
public class ConsumerConfiguration {

}
