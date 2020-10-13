package com.polaris.core.config.properties;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.core.Ordered;

import com.polaris.core.config.Config;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.annotation.PolarisConfigurationProperties;
import com.polaris.core.config.annotation.PolarisMultiConfigurationProperties;
import com.polaris.core.config.provider.ConfHandlerFactory;
import com.polaris.core.config.provider.ConfigFactory;
import com.polaris.core.exception.ConfigException;
import com.polaris.core.util.BeanUtil;
import com.polaris.core.util.StringUtil;

public class ConfigurationProperties extends ConfigurationAbs {
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationProperties.class);
	private Set<ConfigurationPropertiesBean> configBeanSet = new HashSet<>();
	public static final String BEAN_NAME = ConfigurationProperties.class.getName();
	
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 1;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
	    PolarisConfigurationProperties annotation = getAnnotation(bean, beanName, PolarisConfigurationProperties.class);
	     
	    if (annotation != null) {
	        postProcessBeforeInitialization0(bean, annotation);
        } else {
            PolarisMultiConfigurationProperties annotations = getAnnotation(bean, beanName, PolarisMultiConfigurationProperties.class);
            if (annotations != null && annotations.value() != null && annotations.value().length > 0) {
                for (PolarisConfigurationProperties annotationElement : annotations.value()) {
                    postProcessBeforeInitialization0(bean, annotationElement);
                }
            }
        }
		return bean;
	}
	
	private void postProcessBeforeInitialization0(Object bean, PolarisConfigurationProperties annotation) {
	    init(bean,annotation);
        configBeanSet.add(new ConfigurationPropertiesBean(bean,annotation));
        bind(bean,annotation);
	}
	
	private void init(Object bean, PolarisConfigurationProperties annotation) {
        if (StringUtil.isNotEmpty(annotation.value())) {
            if (ConfigFactory.get(Type.EXT).getProperties(getPropertiesKey(annotation)) == null) {
                if (annotation.autoRefreshed()) {
                    if (ConfHandlerFactory.get(Type.EXT).getAndListen(annotation.group(),annotation.value())==null) {
                        throw new ConfigException("type:EXT file:" + getPropertiesKey(annotation) + " is not exsit");
                    }
                } else {
                    if (ConfHandlerFactory.get(Type.EXT).get(annotation.group(),annotation.value())==null) {
                        throw new ConfigException("type:EXT file:" + getPropertiesKey(annotation) + " is not exsit");
                    }
                }
                
            } 
        } 
    }
	
	protected void bind(Object bean, PolarisConfigurationProperties annotation) {
	    if (!annotation.bind()) {
	        return;
	    }
	    Properties properties = ConfigFactory.get(Type.EXT).getProperties(getPropertiesKey(annotation));
		Map<String, String> bindMap = new LinkedHashMap<>();
		if (StringUtil.isNotEmpty(annotation.prefix())) {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				if (entry.getKey().toString().startsWith(annotation.prefix() + ".")) {
					bindMap.put(entry.getKey().toString().substring(annotation.prefix().length() + 1), entry.getValue().toString());
				}
			}
			
		} else {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				bindMap.put(entry.getKey().toString(), entry.getValue().toString());
			}
		}
		try {
			BeanUtil.toBean(bean, bindMap, false);
		} catch (Exception ex) {
			logger.error("ERROR:",ex);
		}
	}
	
	private String getPropertiesKey(PolarisConfigurationProperties annotation) {
	    return Config.merge(annotation.group(), annotation.value());
	}

	protected Set<ConfigurationPropertiesBean> getConfigBeanSet() {
		return configBeanSet;
	}
	
	static class ConfigurationPropertiesBean {
		Object object;
		PolarisConfigurationProperties annotation;
		ConfigurationPropertiesBean(Object object,PolarisConfigurationProperties annotation) {
			this.object = object;
			this.annotation = annotation;
		}
		public Object getObject() {
			return object;
		}
		public PolarisConfigurationProperties getAnnotation() {
			return annotation;
		}
	}

}
