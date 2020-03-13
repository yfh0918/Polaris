package com.polaris.core.config.properties;

import com.google.common.collect.Multimap;
import com.polaris.core.config.ConfEndPoint;
import com.polaris.core.config.properties.ConfigurationProperties.ConfigurationPropertiesBean;
import com.polaris.core.util.SpringUtil;

public class ConfigurationPropertiesEndPoint implements ConfEndPoint{
	
	@Override
	public void onComplete() {
		ConfigurationProperties configurationProperties = SpringUtil.getBean(ConfigurationProperties.class);
		if (configurationProperties == null) {
			return;
		}
		Multimap<String, ConfigurationPropertiesBean> annotationMap = configurationProperties.getAnnotationMap();
		for (ConfigurationPropertiesBean bean : annotationMap.values()) {
			if (bean.getAnnotation().autoRefreshed()) {
				configurationProperties.fieldSet(bean.getObject(), bean.getAnnotation());
			}
		}
	}
	
}
