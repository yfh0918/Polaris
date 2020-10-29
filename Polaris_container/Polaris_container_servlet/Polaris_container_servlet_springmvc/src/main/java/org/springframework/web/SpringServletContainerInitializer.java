package org.springframework.web;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.polaris.container.config.ConfigurationHelper;
import com.polaris.container.servlet.initializer.ServletContextHelper;
import com.polaris.container.servlet.initializer.WebComponentFactory;
import com.polaris.container.servlet.initializer.WebComponentFactory.WebComponent;
import com.polaris.core.util.SpringContextHealper;

public class SpringServletContainerInitializer implements ServletContainerInitializer {

	@Override
	public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        new SpringMvcInnerInitializer().onStartup(ctx);
	}
	
	@EnableWebMvc
    protected static class SpringMvcInnerInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
        private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
        private WebApplicationContext context;

        @Override
        public void onStartup(ServletContext servletContext) throws ServletException {
            if (!initialized.compareAndSet(false, true)) {
                return;
            }
            super.onStartup(servletContext);
            ServletContextHelper.setServletContext((ConfigurableApplicationContext)context, servletContext);
            SpringContextHealper.setApplicationContext((ConfigurableApplicationContext)context);
            WebComponentFactory.init((ConfigurableApplicationContext)context, servletContext, 
                    WebComponent.INIT,WebComponent.LISTENER,WebComponent.FILTER);
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
    }

}
