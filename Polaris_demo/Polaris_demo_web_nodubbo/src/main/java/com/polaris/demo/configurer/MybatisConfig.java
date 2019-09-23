//package com.banyan.common.configurer;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
//import javax.sql.DataSource;
//
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.core.io.support.ResourcePatternResolver;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import com.github.pagehelper.PageInterceptor;
//import com.polaris.core.config.ConfClient;
//import com.polaris.core.datasource.DynamicDataSource;
//import com.zaxxer.hikari.HikariDataSource;
//
//import tk.mybatis.spring.mapper.MapperScannerConfigurer;
//
//@Configuration
//@EnableTransactionManagement(proxyTargetClass=true)
//public class MybatisConfig {
//	
//	@Primary
//	@Bean(name = "masterDataSource")    
//    public DataSource masterDataSource() {    
//		HikariDataSource dataSource = new HikariDataSource();
//		
//        dataSource.setDriverClassName("com.mysql.jdbc.Driver"); 
//        dataSource.setJdbcUrl(ConfClient.get("jdbc.url"));
//        dataSource.setUsername(ConfClient.get("jdbc.username"));    
//        dataSource.setPassword(ConfClient.get("jdbc.password"));   
//        dataSource.setReadOnly(false);
//        dataSource.setConnectionTimeout(30000);
//        dataSource.setIdleTimeout(600000);
//        dataSource.setMaxLifetime(1800000);
//        dataSource.setMaximumPoolSize(10);
//        dataSource.setMinimumIdle(2);
//        return dataSource;    
//    }
//	
//	@Bean(name = "dynamicDataSource") 
//	public DataSource dataSource(@Autowired @Qualifier("masterDataSource") DataSource primary) {
//        
//		DynamicDataSource dataSource = new DynamicDataSource();
//		Map<Object, Object> targetDataSources = new HashMap<>();
//		targetDataSources.put("default", primary);
//		dataSource.setTargetDataSources(targetDataSources);
//		dataSource.setDefaultTargetDataSource(targetDataSources.get("default"));
//		return dataSource;
//	}
//	
//    @Bean(name = "dataSourceTransactionManager")  
//    public DataSourceTransactionManager dataSourceTransactionManager(@Autowired @Qualifier("dynamicDataSource") DataSource dynamicDataSource) {    
//        return new DataSourceTransactionManager(dynamicDataSource);    
//    }  
//	
//	@Bean(name = "sqlSessionFactoryBean")
//    public SqlSessionFactory sqlSessionFactoryBean(@Autowired @Qualifier("dynamicDataSource") DataSource dynamicDataSource) throws Exception {
//        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
//        factory.setDataSource(dynamicDataSource);
//        factory.setTypeAliasesPackage("com.isea533.mybatis.model");
// 
//        //配置分页插件，详情请查阅官方文档
//        PageInterceptor pageHelper = new com.github.pagehelper.PageInterceptor();
//        Properties properties = new Properties();
//        properties.setProperty("pageSizeZero", "true");//分页尺寸为0时查询所有纪录不再执行分页
//        properties.setProperty("reasonable", "true");//页码<=0 查询第一页，页码>=总页数查询最后一页
//        properties.setProperty("supportMethodsArguments", "true");//支持通过 Mapper 接口参数来传递分页参数
//        properties.setProperty("offsetAsPageNum", "true");
//        properties.setProperty("rowBoundsWithCount", "true");
//        properties.setProperty("returnPageInfo", "check");
//        pageHelper.setProperties(properties);
//
//        //添加插件
//        factory.setPlugins(new Interceptor[]{pageHelper});
//        factory.setPlugins(new Interceptor[]{new com.polaris.core.interceptor.MybatisInterceptor()});
//        
// 
//        //添加XML目录
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        factory.setMapperLocations(resolver.getResources("classpath*:/mappers/**/*.xml"));
//        return factory.getObject();
//    }
// 
//    @Bean
//    public MapperScannerConfigurer mapperScannerConfigurer() {
//    	
//        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
//        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
//        //Mapper接口目录，具体的mapper
//        mapperScannerConfigurer.setBasePackage("com.banyan.common.mapper");
// 
//        //配置通用Mapper，详情请查阅官方文档
//        Properties properties = new Properties();
//        properties.setProperty("mappers", "com.banyan.common.IBaseMapper");
//        properties.setProperty("notEmpty", "false");//insert、update是否判断字符串类型!='' 即 test="str != null"表达式内是否追加 and str != ''
//        properties.setProperty("IDENTITY", "MYSQL");
//        mapperScannerConfigurer.setProperties(properties);
// 
//        return mapperScannerConfigurer;
//    }
//}
