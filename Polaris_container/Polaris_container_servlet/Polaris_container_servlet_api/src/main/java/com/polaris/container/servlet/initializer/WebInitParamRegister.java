package com.polaris.container.servlet.initializer;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.core.component.Initial;

public class WebInitParamRegister implements Initial{
    private static Map<String, String> webInitParamMap = new LinkedHashMap<>();
    protected ConfigurableApplicationContext springContext;
    protected ServletContext servletContext;

    public WebInitParamRegister(ConfigurableApplicationContext springContext, ServletContext servletContext) {
        this.springContext = springContext;
        this.servletContext = servletContext;
    }
    
    @Override
    public void init() {
        for (Map.Entry<String, String> entry : webInitParamMap.entrySet()) {
            servletContext.setInitParameter(entry.getKey(), entry.getValue());
        }
    }
    
    
    public static void register(String key, String value) {
        webInitParamMap.put(key,value);
    }
}
