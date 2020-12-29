package com.polaris.container;

import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.container.config.ConfigurationProxy;
import com.polaris.core.util.SpringContextHealper;

public class SpringContextServer implements Server{
	
    @Override
    public void start() throws Exception {
        ConfigurableApplicationContext context = SpringContextHealper.createApplicationContext(ConfigurationProxy.INSTANCE.getConfiguration());
        context.refresh();
    }
    
    @Override
    public void stop() throws Exception {
    	ConfigurableApplicationContext context = SpringContextHealper.getApplicationContext();
        if (context != null) {
           context.close();
        }
    }
    
    @Override
    public ConfigurableApplicationContext getContext() {
    	return SpringContextHealper.getApplicationContext();
    }
}
