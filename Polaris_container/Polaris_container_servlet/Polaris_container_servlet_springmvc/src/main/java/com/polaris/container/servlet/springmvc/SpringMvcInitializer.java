package com.polaris.container.servlet.springmvc;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.polaris.container.config.ConfigurationHelper;
import com.polaris.container.servlet.ServletOrder;
import com.polaris.container.servlet.initializer.ServletContextHelper;
import com.polaris.container.servlet.initializer.WebServletInitializerExtension;

@Order(ServletOrder.SPRINGMVC)
public class SpringMvcInitializer implements WebServletInitializerExtension { 
	final static Logger logger = LoggerFactory.getLogger(SpringMvcInitializer.class);
	SpringMvcInnerInitializer initializer = null;

	@Override
    public void onStartup(ServletContext servletContext) {
	    initializer = new SpringMvcInnerInitializer(); 
        try {
            initializer.onStartup(servletContext);
        } catch (ServletException e) {
            logger.error("SpringMvcInnerInitializer onStartup Error:{}",e);
        }
    }
	
    

	@EnableWebMvc
	protected static class SpringMvcInnerInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
		private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
		private ServletContext servletContext;
		private WebApplicationContext context;

		@Override
		public void onStartup(ServletContext servletContextImpl) throws ServletException {
			if (!initialized.compareAndSet(false, true)) {
	            return;
	        }
			servletContext = servletContextImpl;
			super.onStartup(servletContext);
			ServletContextHelper.loadServletContext((ConfigurableApplicationContext)context, servletContext,false);
		}
		
		@Override
		protected WebApplicationContext createRootApplicationContext() {
			context = super.createRootApplicationContext();
			return context;
		}
		
		@Override
		protected Class<?>[] getRootConfigClasses() {
			return ConfigurationHelper.getConfiguration(SpringMvcInnerInitializer.class);
		}

		@Override
		protected Class<?>[] getServletConfigClasses() {
			return null;
		}

		@Override
		protected String[] getServletMappings() {
			return new String[] { "/" };
		}
		
		public static boolean isInitialized () {
			return initialized.get();
		}
	}
}
