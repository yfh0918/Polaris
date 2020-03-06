package com.polaris.extension.db.config;

import com.polaris.container.config.ConfigurationExtension;

public class DBConfigurer implements ConfigurationExtension{

	@Override
	public Class<?>[] getConfigurations() {
		return new Class<?>[]{DataSourceConfig.class,MybatisConfigurer.class};
	}

}
