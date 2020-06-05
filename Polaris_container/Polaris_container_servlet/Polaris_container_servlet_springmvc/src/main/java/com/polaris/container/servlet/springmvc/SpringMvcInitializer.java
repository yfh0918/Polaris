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
import com.polaris.container.servlet.ServletContextHelper;
import com.polaris.container.servlet.ServletOrder;
import com.polaris.container.servlet.initializer.ExtensionInitializerAbs;

@Order(ServletOrder.SPRINGMVC)
public class SpringMvcInitializer extends  ExtensionInitializerAbs { 
	final static Logger logger = LoggerFactory.getLogger(SpringMvcInitializer.class);
	SpringMvcInnerInitializer initializer = null;

	@Override
	public void addInitParameter() {
		super.addInitParameter();
	}

	@Override
	public void addListener() {
		super.addListener();
	}

	@Override
	public void addFilter() {
		super.addFilter();
	} 
	
    @Override
    public void addServlet() {
        initializer = new SpringMvcInnerInitializer(); 
        try {
            initializer.onStartup(this.servletContext);
        } catch (ServletException e) {
            logger.error("SpringMvcInnerInitializer onStartup Error:{}",e);
        }
    } 

	@EnableWebMvc
	protected static class SpringMvcInnerInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
		private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
		private ServletContext servletContext;

		@Override
		public void onStartup(ServletContext servletContext) throws ServletException {
			if (!initialized.compareAndSet(false, true)) {
	            return;
	        }
			this.servletContext = servletContext;
			super.onStartup(servletContext);
		}
		
		@Override
		protected WebApplicationContext createRootApplicationContext() {
			WebApplicationContext context = super.createRootApplicationContext();
			ServletContextHelper.loadServletContext((ConfigurableApplicationContext)context, this.servletContext,false);
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
