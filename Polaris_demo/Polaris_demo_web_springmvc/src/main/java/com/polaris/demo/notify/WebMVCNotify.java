package com.polaris.demo.notify;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import com.polaris.extension.mvc.AbstractMVCConfigurerEventListener;
import com.polaris.extension.mvc.MVCConfigurerEvent;
 
public class WebMVCNotify extends AbstractMVCConfigurerEventListener {
	
	@Override
	public void onMVCEvent(MVCConfigurerEvent event) {
		ResourceHandlerRegistry registry = 
				event.getEventParameter(ResourceHandlerRegistry.class);
		if (registry != null ) {
			//支持静态资源访问
			registry.addResourceHandler("/**")
	        	.addResourceLocations("classpath:/resources/")
	        	.addResourceLocations("classpath:/static/")
	        	.addResourceLocations("classpath:/public/");
		}
	}


}
