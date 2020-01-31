package com.polaris.container.servlet.springmvc;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.polaris.container.servlet.ServletOrder;
import com.polaris.container.servlet.initializer.ExtensionInitializerAbs;
import com.polaris.core.ConfigurationLoader;
import com.polaris.core.util.SpringUtil;

@Order(ServletOrder.SPRINGMVC)
public class SpringMvcInitializer extends  ExtensionInitializerAbs { 
	final static Logger logger = LoggerFactory.getLogger(SpringMvcInitializer.class);
	InnerInitializer initializer = null;

	@Override
	public void loadContext() {
		initializer = new InnerInitializer(); 
		try {
			initializer.onStartup(this.servletContext);
		} catch (ServletException e) {
			logger.error(e.getMessage());
		}
	} 

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
	
	public static class InnerInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
		private static volatile AtomicBoolean initialized = new AtomicBoolean(false);

		@Override
		public void onStartup(ServletContext servletContext) throws ServletException {
			if (!initialized.compareAndSet(false, true)) {
	            return;
	        }
			super.onStartup(servletContext);
		}
		
		@Override
		protected WebApplicationContext createRootApplicationContext() {
			WebApplicationContext context = super.createRootApplicationContext();
			if (context instanceof AnnotationConfigWebApplicationContext) {
				((AnnotationConfigWebApplicationContext)context).registerShutdownHook();
			}
			SpringUtil.setApplicationContext(context);
			return context;
		}
		
		@Override
		protected Class<?>[] getRootConfigClasses() {
			return ConfigurationLoader.getRootConfigClass();
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
