package com.polaris.container;

import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.container.config.ConfigurationHelper;
import com.polaris.core.util.SpringUtil;

public class SpringContextServer implements Server{
	
    @Override
    public void start() throws Exception {
    	SpringUtil.refresh(ConfigurationHelper.getConfiguration());
    }
    
    @Override
    public void stop() throws Exception {
    	ConfigurableApplicationContext context = SpringUtil.getApplicationContext();
        if (context != null) {
           context.close();
        }
    }
    
    @Override
    public Object getContext() {
    	return SpringUtil.getApplicationContext();
    }
}
