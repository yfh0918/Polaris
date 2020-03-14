package com.polaris.core.config.properties;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotatedElementUtils;

import com.polaris.core.config.provider.ConfCompositeProvider;
import com.polaris.core.util.JsonUtil;
import com.polaris.core.util.StringUtil;

public class ConfigurationProperties implements BeanPostProcessor, PriorityOrdered, ApplicationContextAware, InitializingBean{
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationProperties.class);
	private Set<ConfigurationPropertiesBean> configBeanSet = new HashSet<>();
	public static final String BEAN_NAME = ConfigurationProperties.class.getName();
	
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 3;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		PolarisConfigurationProperties annotation = getAnnotation(bean, beanName, PolarisConfigurationProperties.class);
		if (annotation != null) {
			configBeanSet.add(new ConfigurationPropertiesBean(bean,annotation));
			bind(bean,annotation);
		}
		return bean;
	}
	
	
	private <A extends Annotation> A getAnnotation(Object bean, String beanName, Class<A> type) {
		return AnnotatedElementUtils.findMergedAnnotation(bean.getClass(), type);
	}
	
	protected void bind(Object bean, PolarisConfigurationProperties annotation) {
		Properties properties = ConfCompositeProvider.INSTANCE.getProperties();
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
			JsonUtil.toBean(bean, bindMap, false);
		} catch (RuntimeException ex) {
			logger.error("ERROR:",ex);
		}
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
