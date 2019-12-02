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
		Class<?>[] rootConfigs = WebConfigInitializer.getRootConfigs();
		if (rootConfigs == null || rootConfigs.length == 0) {
			return new Class<?>[]{DefaultRootConfig.class};
		}
		Class<?>[] newRootConfigs = new Class<?>[rootConfigs.length+1];
		newRootConfigs[0] = DefaultRootConfig.class;
		for (int i0 = 0; i0 < rootConfigs.length; i0++) {
			newRootConfigs[i0 + 1] = rootConfigs[i0];
		}
		return newRootConfigs;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return WebConfigInitializer.getWebConfigs();
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}
	
	public static boolean isInitialized () {
		return initialized.get();
	}
	

}
