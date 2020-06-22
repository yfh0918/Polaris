package com.polaris.container.servlet.initializer;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebInitParam;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

public class WebInitParamRegister extends WebComponentRegister{
    private static Map<String, String> webInitParamMap = new LinkedHashMap<>();

    public WebInitParamRegister(ConfigurableApplicationContext springContext, ServletContext servletContext) {
        super(springContext,servletContext,WebInitParam.class);
    }
    
    @Override
    public void init() {
        super.init();
        addInitParamToServletContext();
    }
    
    
    public static void register(String key, String value) {
        webInitParamMap.put(key,value);
    }

    @Override
    protected void doRegister(Class<?> type, Map<String, Object> attributes, ScannedGenericBeanDefinition beanDefinition) {
        String name = (String) attributes.get("name");
        String value = (String) attributes.get("value");
        webInitParamMap.put(name, value);
    }
    
    private void addInitParamToServletContext() {
        for (Map.Entry<String, String> entry : webInitParamMap.entrySet()) {
            servletContext.setInitParameter(entry.getKey(), entry.getValue());
        }
    }
}
