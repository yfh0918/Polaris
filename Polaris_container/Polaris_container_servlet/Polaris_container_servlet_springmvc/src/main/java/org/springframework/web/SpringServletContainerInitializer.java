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

public class SpringServletContainerInitializer implements ServletContainerInitializer {

	@Override
	public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        new SpringMvcInnerInitializer().onStartup(ctx);
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
    }

}
