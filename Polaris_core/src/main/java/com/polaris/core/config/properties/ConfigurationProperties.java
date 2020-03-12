package com.polaris.core.config.properties;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

import com.polaris.core.config.ConfClient;
import com.polaris.core.util.ReflectionUtil;
import com.polaris.core.util.StringUtil;

public class ConfigurationProperties implements BeanPostProcessor, PriorityOrdered, ApplicationContextAware, InitializingBean{
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationProperties.class);
	private Map<Object, PolarisConfigurationProperties> annotationMap = new ConcurrentHashMap<>();
	public static final String BEAN_NAME = ConfigurationProperties.class.getName();
	
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 1;
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
			annotationMap.put(bean,annotation);
			bind(bean,annotation);
		}
		return bean;
	}
	
	
	private <A extends Annotation> A getAnnotation(Object bean, String beanName, Class<A> type) {
		return AnnotationUtils.findAnnotation(bean.getClass(), type);
	}
	
	private void bind(Object bean, PolarisConfigurationProperties annotation) {
		fieldSet(bean, annotation,null,null);
	}

	protected void fieldSet(Object bean, PolarisConfigurationProperties annotation, String key, String value) {
		ReflectionUtils.doWithMethods(bean.getClass(), new MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				String fileName = ReflectionUtil.getFieldNameForSet(method);
				if (StringUtil.isEmpty(fileName)) {
					return;
				}
				if (StringUtil.isNotEmpty(annotation.value())) {
					fileName = annotation.value()+ "." + fileName;
				}
				if (StringUtil.isNotEmpty(key) && !fileName.equals(key)) {
					return;
				}
				String  configValue = null;
				if (StringUtil.isEmpty(key)) {
					configValue = ConfClient.get(fileName);
					if (configValue == null) {
						return;
					}
				}
				try {
					ReflectionUtil.setMethodValue(method, bean, value != null? value : configValue);
				} catch (RuntimeException ex) {
					if (!annotation.ignoreInvalidFields()) {
						throw ex;
					}
					logger.warn("class:{} fileName:{} value:{} is incorrect",bean.getClass().getName(), fileName,value != null? value : configValue);
				}
			}
		});
	}

	protected Map<Object,PolarisConfigurationProperties> getAnnotationMap() {
		return annotationMap;
	}

}
