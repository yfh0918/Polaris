package com.polaris.springmvc;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.polaris.core.config.DefaultRootConfig;
import com.polaris.core.util.SpringUtil;
import com.polaris.http.initializer.WebConfigInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	private static AtomicBoolean initialized = new AtomicBoolean(false);

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
		SpringUtil.setApplicationContext(context);
		return context;
	}
	
	@Override
	protected Class<?>[] getRootConfigClasses() {
		Class<?> rootConfigClass = WebConfigInitializer.getRootConfigClass();
		if (rootConfigClass == null) {
			return new Class<?>[]{DefaultRootConfig.class};
		}
		return new Class<?>[]{DefaultRootConfig.class,rootConfigClass};
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
