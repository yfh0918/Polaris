package com.polaris.workflow.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.polaris.core.datasource.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {
	
	@Value("${db.driverClassName}")
	private String driverClassName;

	@Value("${db.url}")
	private String jdbcUrl;
	
	@Value("${db.username}")
	private String username;
	
	@Value("${db.password}")
	private String password;

	@Primary
	@Bean(name = "masterDataSource")    
    public DataSource masterDataSource() {    
		HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClassName); 
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);    
        dataSource.setPassword(password);   
        dataSource.setReadOnly(false);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        return dataSource;    
    }
	
	@Bean(name = "dataSource") 
	public DataSource dataSource(@Autowired @Qualifier("masterDataSource") DataSource primary) {
        
		DynamicDataSource dataSource = new DynamicDataSource();
		Map<Object, Object> targetDataSources = new HashMap<>();
		targetDataSources.put("default", primary);
		dataSource.setTargetDataSources(targetDataSources);
		dataSource.setDefaultTargetDataSource(targetDataSources.get("default"));
		return dataSource;
	}
	

}
