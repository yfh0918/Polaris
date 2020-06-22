package com.polaris.container.servlet.initializer;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

import com.polaris.container.servlet.filter.TraceFilter;
import com.polaris.core.exception.ServletContextException;

public class WebFilterRegister extends WebComponentRegister{
    private static final List<WebFilterBean> filterBeans = new CopyOnWriteArrayList<>();
    
    public WebFilterRegister(ConfigurableApplicationContext springContext, ServletContext servletContext) {
        super(springContext,servletContext,WebFilter.class);
    }
    
    @Override
    public void init() {
        super.init();
        addFilterToServletContext();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void doRegister(Class<?> type, Map<String, Object> attributes, ScannedGenericBeanDefinition beanDefinition) {
        WebFilterBean filterBean = new WebFilterBean();
        filterBean.setAsyncSupported((Boolean)attributes.get("asyncSupported"));
        filterBean.setDispatcherTypes(extractDispatcherTypes(attributes));
        filterBean.setFilterName(determineName(attributes, beanDefinition,"filterName"));
        try {
            Class<? extends Filter> filterClass = (Class<? extends Filter>)(Class.forName(beanDefinition.getBeanClassName()));
            Order order = AnnotationUtils.findAnnotation(filterClass, Order.class);
            if (order != null) {
                filterBean.setOrder(order.value());
            }
            filterBean.setFilterClass(filterClass);
        } catch (ClassNotFoundException e) {
            throw new ServletContextException(beanDefinition.getBeanClassName() + " is not found");
        }
        filterBean.setInitParams(extractInitParameters(attributes));
        filterBean.setUrlPatterns(extractUrlPatterns(attributes));
        register(filterBean);    
    }
    
    private void addFilterToServletContext() {
        FilterRegistration.Dynamic dynamic = servletContext.addFilter("TraceFilter", new TraceFilter());
        dynamic.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        for (WebFilterBean filterBean : filterBeans) {
            if (filterBean.getFilter() == null) {
                dynamic = servletContext.addFilter(filterBean.getFilterName(),filterBean.getFilterClass());
            } else {
                dynamic = servletContext.addFilter(filterBean.getFilterName(),filterBean.getFilter());
            }
            dynamic.setAsyncSupported(filterBean.isAsyncSupported());
            dynamic.addMappingForUrlPatterns(filterBean.getDispatcherTypes(), true, filterBean.getUrlPatterns());
            for (Map.Entry<String, String> entry : filterBean.getInitParams().entrySet()) {
                dynamic.setInitParameter(entry.getKey(),entry.getValue());
            }
            if (filterBean.getServletNames() != null && filterBean.getServletNames().length > 0) {
                dynamic.addMappingForServletNames(filterBean.getDispatcherTypes(), true, filterBean.getServletNames());
            }
        }
    }
    
    public static void register(WebFilterBean filterBean) {
        filterBeans.add(filterBean);
        Collections.sort(filterBeans, new WebFilterBeanCompare());
    }
    
    static public class WebFilterBean {
        
        /**
         * The init parameters of the filter
         *
         * @return the init parameters of the filter
         */
        private Map<String, String> initParams = new HashMap<>();
        
        /**
         * The name of the filter
         *
         * @return the name of the filter
         */
        private String filterName = "";
        

        /**
         * The names of the servlets to which the filter applies.
         *
         * @return the names of the servlets to which the filter applies
         */
        private String[] servletNames = {};

        /**
         * The URL patterns to which the filter applies
         *
         * @return the URL patterns to which the filter applies
         */
        private String[] urlPatterns = {"/*"};

        /**
         * The dispatcher types to which the filter applies
         *
         * @return the dispatcher types to which the filter applies
         */
        private EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST);
        
        /**
         * Declares whether the filter supports asynchronous operation mode.
         *
         * @return {@code true} if the filter supports asynchronous operation mode
         * @see javax.servlet.ServletRequest#startAsync
         * @see javax.servlet.ServletRequest#startAsync(ServletRequest,
         * ServletResponse)
         */
        private boolean asyncSupported = false;
        
        private Class<? extends Filter> filterClass;
        
        private Filter filter;
        
        private Integer order = Ordered.LOWEST_PRECEDENCE;
        
        public Map<String, String> getInitParams() {
            return initParams;
        }

        public void setInitParams(Map<String, String> initParams) {
            this.initParams = initParams;
        }

        public String getFilterName() {
            return filterName;
        }

        public void setFilterName(String filterName) {
            this.filterName = filterName;
        }

        public String[] getServletNames() {
            return servletNames;
        }

        public void setServletNames(String[] servletNames) {
            this.servletNames = servletNames;
        }

        public String[] getUrlPatterns() {
            return urlPatterns;
        }

        public void setUrlPatterns(String[] urlPatterns) {
            this.urlPatterns = urlPatterns;
        }

        public EnumSet<DispatcherType> getDispatcherTypes() {
            return dispatcherTypes;
        }

        public void setDispatcherTypes(EnumSet<DispatcherType> dispatcherTypes) {
            this.dispatcherTypes = dispatcherTypes;
        }

        public boolean isAsyncSupported() {
            return asyncSupported;
        }

        public void setAsyncSupported(boolean asyncSupported) {
            this.asyncSupported = asyncSupported;
        }

        public Class<? extends Filter> getFilterClass() {
            return filterClass;
        }

        public void setFilterClass(Class<? extends Filter> filterClass) {
            this.filterClass = filterClass;
        }

        public Filter getFilter() {
            return filter;
        }

        public void setFilter(Filter filter) {
            this.filter = filter;
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }
    }
    
    static public class WebFilterBeanCompare implements Comparator<WebFilterBean> {
        @Override
        public int compare(WebFilterBean o1, WebFilterBean o2) {
            return o1.getOrder().compareTo(o2.getOrder());
        }
    }
}
