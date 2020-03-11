package com.polaris.core.config.properties;

import java.util.Map;

import com.polaris.core.config.ConfEndPoint;
import com.polaris.core.util.SpringUtil;
import com.polaris.core.util.StringUtil;

public class ConfigurationPropertiesEndPoint implements ConfEndPoint{
	@Override
	public void put(String type, String file, String key, String value) {
		ConfigurationProperties configurationProperties = SpringUtil.getBean(ConfigurationProperties.class);
		if (configurationProperties == null) {
			return;
		}
		for (Map.Entry<Object,PolarisConfigurationProperties> entry : configurationProperties.getAnnotationMap().entrySet()) {
			if (type.equals(entry.getValue().type()) && 
					(StringUtil.isEmpty(entry.getValue().file()) || file.equals(entry.getValue().file()))) {
				configurationProperties.fieldSet(entry.getKey(), entry.getValue(), key, value);
			}
		}
	}
}
