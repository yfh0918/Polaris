package com.polaris.resteasy;

import java.util.Map;

import javax.servlet.ServletRegistration;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.polaris.core.util.SpringUtil;
import com.polaris.http.initializer.AbsHttpInitializer;
import com.polaris.http.initializer.WebConfigInitializer;

public class Initializer extends  AbsHttpInitializer { 
	private AnnotationConfigApplicationContext applicationContext = null;

	@Override
	public void loadContext() {
		applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(WebConfigInitializer.getRootConfigs());
		applicationContext.refresh();
	} 
	
	@Override
	public void addInitParameter() {
		Map<String,Object> providers = SpringUtil.getApplicationContext().getBeansWithAnnotation(Provider.class);  
		boolean first = true;
		StringBuilder builder = new StringBuilder();
        for (Object provider : providers.values()) {
           if (first) {
              first = false;
           } else {
              builder.append(",");
           }
           builder.append(provider.getClass().getName());
        }
        servletContext.setInitParameter(ResteasyContextParameters.RESTEASY_SCANNED_PROVIDERS, builder.toString());

		Map<String,Object> resources = SpringUtil.getApplicationContext().getBeansWithAnnotation(Path.class);  
		first = true;
		builder = new StringBuilder();
        for (Object resource : resources.values()) {
           if (first) {
              first = false;
           } else {
              builder.append(",");
           }
           builder.append(resource.getClass().getName());
        }
        servletContext.setInitParameter(ResteasyContextParameters.RESTEASY_SCANNED_RESOURCES, builder.toString());
        
        super.addInitParameter();

	}

	@Override
	public void addListener() {
		servletContext.addListener(org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap.class);
		super.addListener();
		
	}

	@Override
	public void addFilter() {
		super.addFilter();
	}

	@Override
	public void addServlet() {
		ServletRegistration.Dynamic servletRegistration = servletContext.
			    addServlet("dispatcher", org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.class);
			    servletRegistration.setLoadOnStartup(1);
			    servletRegistration.addMapping("/*");	
	}

}
