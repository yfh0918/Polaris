package com.polaris.container;

import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.container.config.ConfigurationHelper;
import com.polaris.core.util.SpringUtil;

public class SpringContextServer implements Server{
	
    public void start() throws Exception {
    	SpringUtil.refresh(ConfigurationHelper.getConfiguration());
    }
    
    public void stop() throws Exception {
    	ConfigurableApplicationContext context = SpringUtil.getApplicationContext();
        if (context != null) {
           context.close();
        }
    }
    
    public Object getContext() {
    	return SpringUtil.getApplicationContext();
    }
}
