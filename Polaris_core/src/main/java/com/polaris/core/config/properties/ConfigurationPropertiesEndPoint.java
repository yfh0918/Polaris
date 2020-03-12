package com.polaris.core.config.properties;

import java.util.Map;

import com.polaris.core.config.ConfEndPoint;
import com.polaris.core.config.Config.Opt;
import com.polaris.core.util.SpringUtil;

public class ConfigurationPropertiesEndPoint implements ConfEndPoint{
	@Override
	public void onChange(String key, String value, Opt opt) {
		if (opt == Opt.DELETE) {
			return;
		}
		ConfigurationProperties configurationProperties = SpringUtil.getBean(ConfigurationProperties.class);
		if (configurationProperties == null) {
			return;
		}
		for (Map.Entry<Object,PolarisConfigurationProperties> entry : configurationProperties.getAnnotationMap().entrySet()) {
			configurationProperties.fieldSet(entry.getKey(), entry.getValue(), key, value);
		}
	}
}
