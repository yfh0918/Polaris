package com.polaris.database.config;

import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.polaris.container.config.ConfigurationExtension;

@Import({DataSourceConfig.class,MybatisConfigurer.class})
@EnableTransactionManagement(proxyTargetClass=true)
public class PolarisDBConfig implements ConfigurationExtension{

}
