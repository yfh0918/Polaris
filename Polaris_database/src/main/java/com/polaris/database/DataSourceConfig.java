package com.polaris.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.github.pagehelper.PageInterceptor;
import com.polaris.core.config.ConfClient;
import com.polaris.core.datasource.DynamicDataSource;
import com.polaris.core.util.StringUtil;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {
	
	@Primary
	@Bean(name = "masterDataSource")    
    public DataSource masterDataSource() { 
		String jdbcUrl = ConfClient.get("jdbc.url",ConfClient.get("spring.datasource.url"));
		String username = ConfClient.get("jdbc.username",ConfClient.get("spring.datasource.username"));
		String password = ConfClient.get("jdbc.password",ConfClient.get("spring.datasource.password"));
		String driver = ConfClient.get("jdbc.driver",ConfClient.get("spring.datasource.driver"));
		if (StringUtil.isEmpty(jdbcUrl) || StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
			return null;
		}
		if (StringUtil.isEmpty(driver)) {
			driver = "com.mysql.jdbc.Driver";
		}
		HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driver); 
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
        if (primary == null) {
        	return null;
        }
		DynamicDataSource dataSource = new DynamicDataSource();
		Map<Object, Object> targetDataSources = new HashMap<>();
		targetDataSources.put("default", primary);
		dataSource.setTargetDataSources(targetDataSources);
		dataSource.setDefaultTargetDataSource(targetDataSources.get("default"));
		return dataSource;
	}
	
    @Bean(name = "transactionManager")  
    public DataSourceTransactionManager transactionManager(@Autowired @Qualifier("dataSource") DataSource dataSource) { 
    	if (dataSource == null) {
    		return null;
    	}
        return new DataSourceTransactionManager(dataSource);    
    }  
	
	@Bean(name = "sqlSessionFactoryBean")
    public SqlSessionFactory sqlSessionFactoryBean(@Autowired @Qualifier("dataSource") DataSource dataSource) throws Exception {
		if (dataSource == null) {
    		return null;
    	}
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTypeAliasesPackage("com.isea533.mybatis.model");
 
        //配置分页插件，详情请查阅官方文档
        PageInterceptor pageHelper = new com.github.pagehelper.PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("pageSizeZero", "true");//分页尺寸为0时查询所有纪录不再执行分页
        properties.setProperty("reasonable", "true");//页码<=0 查询第一页，页码>=总页数查询最后一页
        properties.setProperty("supportMethodsArguments", "true");//支持通过 Mapper 接口参数来传递分页参数
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("autoRuntimeDialect", "true");
        pageHelper.setProperties(properties);
        
        //添加插件
        factory.setPlugins(new Interceptor[]{pageHelper,new com.polaris.core.interceptor.MybatisInterceptor()});
        
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factory.setMapperLocations(resolver.getResources("classpath*:/mappers/**/*.xml"));
        return factory.getObject();
    }
}
