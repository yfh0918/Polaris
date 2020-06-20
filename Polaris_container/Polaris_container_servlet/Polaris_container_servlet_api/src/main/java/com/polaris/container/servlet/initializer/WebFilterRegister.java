package com.polaris.container.servlet.initializer;

import java.util.Arrays;
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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.polaris.container.servlet.filter.TraceFilter;
import com.polaris.core.exception.ServletContextException;

public class WebFilterRegister extends WebComponentRegister{
    private static List<WebFilterBean> filterBeans = new CopyOnWriteArrayList<>();
    
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
    protected void doRegister(Map<String, Object> attributes, ScannedGenericBeanDefinition beanDefinition) {
        WebFilterBean filterBean = new WebFilterBean();
        filterBean.setAsyncSupported((Boolean)attributes.get("asyncSupported"));
        filterBean.setDispatcherTypes(extractDispatcherTypes(attributes));
        filterBean.setFilterName(determineName(attributes, beanDefinition));
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
        register(filterBean);    }
    
    private Map<String, String> extractInitParameters(
            Map<String, Object> attributes) {
        Map<String, String> initParameters = new HashMap<>();
        for (AnnotationAttributes initParam : (AnnotationAttributes[]) attributes
                .get("initParams")) {
            String name = (String) initParam.get("name");
            String value = (String) initParam.get("value");
            initParameters.put(name, value);
        }
        return initParameters;
    }
    
    private EnumSet<DispatcherType> extractDispatcherTypes(
            Map<String, Object> attributes) {
        DispatcherType[] dispatcherTypes = (DispatcherType[]) attributes
                .get("dispatcherTypes");
        if (dispatcherTypes.length == 0) {
            return EnumSet.noneOf(DispatcherType.class);
        }
        if (dispatcherTypes.length == 1) {
            return EnumSet.of(dispatcherTypes[0]);
        }
        return EnumSet.of(dispatcherTypes[0],
                Arrays.copyOfRange(dispatcherTypes, 1, dispatcherTypes.length));
    }

    private String determineName(Map<String, Object> attributes,
            BeanDefinition beanDefinition) {
        return (String) (StringUtils.hasText((String) attributes.get("filterName"))
                ? attributes.get("filterName") : beanDefinition.getBeanClassName());
    }

    private String[] extractUrlPatterns(Map<String, Object> attributes) {
        String[] value = (String[]) attributes.get("value");
        String[] urlPatterns = (String[]) attributes.get("urlPatterns");
        if (urlPatterns.length > 0) {
            Assert.state(value.length == 0,
                    "The urlPatterns and value attributes are mutually exclusive.");
            return urlPatterns;
        }
        return value;
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
        }
    }
    
    public static void register(WebFilterBean filterBean) {
        filterBeans.add(filterBean);
        Collections.sort(filterBeans, new WebFilterBeanCompare());
    }
    
    static public class WebFilterBean {
        /**
         * The description of the filter
         * 
         * @return the description of the filter
         */
        private String description = "";
        
        /**
         * The display name of the filter
         *
         * @return the display name of the filter
         */
        private String displayName = "";
        
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
         * The small-icon of the filter
         *
         * @return the small-icon of the filter
         */
        private String smallIcon =  "";

        /**
         * The large-icon of the filter
         *
         * @return the large-icon of the filter
         */
        private String largeIcon = "";

        /**
         * The names of the servlets to which the filter applies.
         *
         * @return the names of the servlets to which the filter applies
         */
        private String[] servletNames = {};
        
        /**
         * The URL patterns to which the filter applies
         * The default value is an empty array.
         *
         * @return the URL patterns to which the filter applies
         */
        private String[] value = {};

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
        
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

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

        public String getSmallIcon() {
            return smallIcon;
        }

        public void setSmallIcon(String smallIcon) {
            this.smallIcon = smallIcon;
        }

        public String getLargeIcon() {
            return largeIcon;
        }

        public void setLargeIcon(String largeIcon) {
            this.largeIcon = largeIcon;
        }

        public String[] getServletNames() {
            return servletNames;
        }

        public void setServletNames(String[] servletNames) {
            this.servletNames = servletNames;
        }

        public String[] getValue() {
            return value;
        }

        public void setValue(String[] value) {
            this.value = value;
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
