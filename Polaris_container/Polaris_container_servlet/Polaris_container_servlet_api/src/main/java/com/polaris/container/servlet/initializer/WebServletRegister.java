package com.polaris.container.servlet.initializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.polaris.core.exception.ServletContextException;

public class WebServletRegister extends WebComponentRegister{
    private final static List<WebServletBean> servletBeans = new CopyOnWriteArrayList<>();
    static {
        WebComponentRegister.TYPE_FILTERS.add(new AnnotationTypeFilter(WebServlet.class));    
    }
    
    public WebServletRegister(ConfigurableApplicationContext springContext, ServletContext servletContext) {
        super(springContext,servletContext,WebServlet.class);
    }
    
    @Override
    public void init() {
        super.init();
        addServletToServletContext();
    }
    
    @Override
    protected void doRegister(Class<?> type, Map<String, Object> attributes, ScannedGenericBeanDefinition beanDefinition) {
        WebServletBean servletBean = new WebServletBean();
        servletBean.setAsyncSupported((Boolean)attributes.get("asyncSupported"));
        servletBean.setInitParams(extractInitParameters(attributes));
        servletBean.setLoadOnStartup((Integer)attributes.get("loadOnStartup"));
        servletBean.setName(determineName(attributes, beanDefinition,"name"));
        servletBean.setServletClassName(beanDefinition.getBeanClassName());
        servletBean.setUrlPatterns(extractUrlPatterns(attributes));
        servletBean.setMultipartConfig(determineMultipartConfig(beanDefinition));
        register(servletBean);    
    }
    
    private MultipartConfigElement determineMultipartConfig(
            ScannedGenericBeanDefinition beanDefinition) {
        Map<String, Object> attributes = beanDefinition.getMetadata()
                .getAnnotationAttributes(MultipartConfig.class.getName());
        if (attributes == null) {
            return null;
        }
        return new MultipartConfigElement((String) attributes.get("location"),
                (Long) attributes.get("maxFileSize"),
                (Long) attributes.get("maxRequestSize"),
                (Integer) attributes.get("fileSizeThreshold"));
    }
    
    private void addServletToServletContext() {
        if (servletBeans.size() == 0) {
            throw new ServletContextException("@WebServlet or WebServletBean is not found");
        }
        
        for (WebServletBean servletBean : servletBeans) {
            ServletRegistration.Dynamic dynamic = servletContext.addServlet(servletBean.getName(), servletBean.getServletClassName());
            dynamic.setAsyncSupported(servletBean.getAsyncSupported());
            dynamic.addMapping(servletBean.getUrlPatterns());
            for (Map.Entry<String, String> entry : servletBean.getInitParams().entrySet()) {
                dynamic.setInitParameter(entry.getKey(),entry.getValue());
            }
            dynamic.setLoadOnStartup(servletBean.getLoadOnStartup());
            if (servletBean.getMultipartConfig() != null) {
                dynamic.setMultipartConfig(servletBean.getMultipartConfig());
            }
        }
    }
    
    public static void register(WebServletBean servletBean) {
        servletBeans.add(servletBean);
    }
    
    static public class WebServletBean {
        
        /**
         * The name of the servlet
         *
         * @return the name of the servlet
         */
        private String name = "";
        
        /**
         * The URL patterns of the servlet
         *
         * @return the URL patterns of the servlet
         */
        private String[] urlPatterns =  {};
        
        /**
         * The load-on-startup order of the servlet 
         *
         * @return the load-on-startup order of the servlet
         */
        private Integer loadOnStartup =  -1;
        
        /**
         * The init parameters of the servlet
         *
         * @return the init parameters of the servlet
         */
        private Map<String, String> initParams =  new HashMap<>();;
        
        /**
         * Declares whether the servlet supports asynchronous operation mode.
         *
         * @return {@code true} if the servlet supports asynchronous operation mode
         * @see javax.servlet.ServletRequest#startAsync
         * @see javax.servlet.ServletRequest#startAsync(ServletRequest,
         * ServletResponse)
         */
        private Boolean asyncSupported =  false;
        
        private MultipartConfigElement multipartConfig;
        
        private String servletClassName;
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String[] getUrlPatterns() {
            return urlPatterns;
        }

        public void setUrlPatterns(String[] urlPatterns) {
            this.urlPatterns = urlPatterns;
        }

        public Integer getLoadOnStartup() {
            return loadOnStartup;
        }

        public void setLoadOnStartup(Integer loadOnStartup) {
            this.loadOnStartup = loadOnStartup;
        }

        public Map<String, String> getInitParams() {
            return initParams;
        }

        public void setInitParams(Map<String, String> initParams) {
            this.initParams = initParams;
        }

        public Boolean getAsyncSupported() {
            return asyncSupported;
        }

        public void setAsyncSupported(Boolean asyncSupported) {
            this.asyncSupported = asyncSupported;
        }

        public MultipartConfigElement getMultipartConfig() {
            return multipartConfig;
        }

        public void setMultipartConfig(MultipartConfigElement multipartConfig) {
            this.multipartConfig = multipartConfig;
        }

        public String getServletClassName() {
            return servletClassName;
        }

        public void setServletClassName(String servletClassName) {
            this.servletClassName = servletClassName;
        }
    }
}
