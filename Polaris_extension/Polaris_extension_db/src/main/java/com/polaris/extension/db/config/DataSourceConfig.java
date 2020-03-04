package com.polaris.extension.db.config;

import java.util.HashMap;
import java.util.List;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.pagehelper.PageInterceptor;
import com.polaris.core.config.ConfClient;
import com.polaris.core.datasource.DynamicDataSource;
import com.polaris.core.util.EncryptUtil;
import com.polaris.core.util.EncryptUtil.Type;
import com.polaris.core.util.StringUtil;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement(proxyTargetClass=true)
public class DataSourceConfig {
	
	private static final String DEAULT_DATASOUCE_KEY = "default";

	@Bean(name = "dataSource") 
	public DataSource dataSource() {
		
		//默认数据源
		String defaultDSName = DEAULT_DATASOUCE_KEY;
		DataSource defaultDS = createDateSource(DEAULT_DATASOUCE_KEY);
		
		//获取多数据源名称
		List<String> dsNames = DBEndPoint.getNames();
		if (dsNames.size() > 0) {
			
			//重新设置默认数据源
			if (defaultDS == null) {
				defaultDSName = dsNames.get(0);//没有默认的获取第一个作为默认的
				defaultDS = createDateSource(defaultDSName);
			}
		}
		
		//没有数据源直接返回
        if (defaultDS == null) {
        	return null;
        }
        
        //设置目标数据源
		Map<Object, Object> targetDataSources = new HashMap<>();
		targetDataSources.put(defaultDSName, defaultDS);//保存默认数据源
		
		//保存多数据源（去除重复的）
		if (dsNames.size() > 0) {
			for (String name : dsNames) {
				if (!targetDataSources.containsKey(name)) {
					targetDataSources.put(name, createDateSource(DEAULT_DATASOUCE_KEY));
				}
			}
		}
		DynamicDataSource dataSource = new DynamicDataSource();
		dataSource.setTargetDataSources(targetDataSources);
		
		//设置默认
		dataSource.setDefaultTargetDataSource(targetDataSources.get(defaultDSName));
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
        factory.setPlugins(new Interceptor[]{pageHelper,new com.polaris.extension.db.interceptor.MybatisInterceptor()});
        
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factory.setMapperLocations(resolver.getResources("classpath*:/mappers/**/*.xml"));
        return factory.getObject();
    }
	

	public static DataSource createDateSource(String key) {
		return createDateSource(createDateSourceParameter(key));
	}
	public static DataSource createDateSource(Map<String, String> parameterMap) {
		if (parameterMap == null) {
			return null;
		}
		return createDateSource(
				parameterMap.get("jdbcUrl"),parameterMap.get("username"),
				parameterMap.get("password"),parameterMap.get("driver"),
				Boolean.parseBoolean(parameterMap.get("readOnly")),Integer.parseInt(parameterMap.get("connectionTimeout")),
				Integer.parseInt(parameterMap.get("idleTimeout")),Integer.parseInt(parameterMap.get("maxLifetime")),
				Integer.parseInt(parameterMap.get("maximumPoolSize")),Integer.parseInt(parameterMap.get("minimumIdle"))
				);
	}
	public static DataSource createDateSource(
			String jdbcUrl, 
			String username, 
			String password, 
			String driver,
			boolean readOnly,
			int connectionTimeout,
			int idleTimeout,
			int maxLifetime,
			int maximumPoolSize,
			int minimumIdle
			) {
		HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);    
        dataSource.setPassword(password);   
        dataSource.setDriverClassName(driver); 
        dataSource.setReadOnly(readOnly);
        dataSource.setConnectionTimeout(connectionTimeout);
        dataSource.setIdleTimeout(idleTimeout);
        dataSource.setMaxLifetime(maxLifetime);
        dataSource.setMaximumPoolSize(maximumPoolSize);
        dataSource.setMinimumIdle(minimumIdle);
        return dataSource;
	}
	
	public static Map<String, String> createDateSourceParameter(String key) {
		Map<String, String> rtnMap = new HashMap<>();
		key = getKey(key);
		String jdbcUrl = ConfClient.get("jdbc"+key+".url",ConfClient.get("spring.datasource"+key+".url"));
		String username = ConfClient.get("jdbc"+key+".username",ConfClient.get("spring.datasource"+key+".username"));
		String password = ConfClient.get("jdbc"+key+".password",ConfClient.get("spring.datasource"+key+".password"));
		if (StringUtil.isEmpty(jdbcUrl) || StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
			return null;
		}
		String driver = ConfClient.get("jdbc"+key+".driver",ConfClient.get("spring.datasource"+key+".driver","com.mysql.cj.jdbc.Driver"));
		rtnMap.put("driver", driver);

		//nullCatalogMeansCurrent=true（mysql8.0以上版本，没有此属性无法自动生成DDL等）
		if (driver.contains("com.mysql.")) {
			if (!jdbcUrl.contains("?")) {
				jdbcUrl=jdbcUrl+"?nullCatalogMeansCurrent=true";
			}
			if (!jdbcUrl.contains("nullCatalogMeansCurrent")) {
				jdbcUrl = jdbcUrl + "&nullCatalogMeansCurrent=true";
			}
			if (!jdbcUrl.contains("useUnicode")) {
				jdbcUrl = jdbcUrl + "&useUnicode=true";
			}
			if (!jdbcUrl.contains("characterEncoding")) {
				jdbcUrl = jdbcUrl + "&characterEncoding=utf-8";
			}
			if (!jdbcUrl.contains("zeroDateTimeBehavior")) {
				jdbcUrl = jdbcUrl + "&zeroDateTimeBehavior=convertToNull";
			}
			if (!jdbcUrl.contains("autoReconnect")) {
				jdbcUrl = jdbcUrl + "&autoReconnect=true";
			}
			if (!jdbcUrl.contains("failOverReadOnly")) {
				jdbcUrl = jdbcUrl + "&failOverReadOnly=false";
			}
		}
		
		String cipherKey = ConfClient.get("jdbc"+key+".cipher.key",ConfClient.get("spring.datasource"+key+".cipher.key",EncryptUtil.getDefaultKey()));
		String startWith = ConfClient.get("jdbc"+key+".cipher.startwith",ConfClient.get("spring.datasource"+key+".cipher.startwith",EncryptUtil.START_WITH));
		username = EncryptUtil.getDecryptValue(startWith,username,EncryptUtil.getInstance(cipherKey,Type.DES));
		password = EncryptUtil.getDecryptValue(startWith,password,EncryptUtil.getInstance(cipherKey,Type.DES));
		rtnMap.put("jdbcUrl", jdbcUrl);
		rtnMap.put("username", username);
		rtnMap.put("password", password);


		//readOnly
		String readOnly = ConfClient.get("jdbc"+key+".readOnly",ConfClient.get("spring.datasource"+key+".readOnly","false"));
		rtnMap.put("readOnly", readOnly);

		//connectionTimeout
		String connectionTimeout = ConfClient.get("jdbc"+key+".connectionTimeout",ConfClient.get("spring.datasource"+key+".connectionTimeout","30000"));
		rtnMap.put("connectionTimeout", connectionTimeout);

		//idleTimeout
		String idleTimeout = ConfClient.get("jdbc"+key+".idleTimeout",ConfClient.get("spring.datasource"+key+".idleTimeout","600000"));
		rtnMap.put("idleTimeout", idleTimeout);

		//idleTimeout
		String maxLifetime = ConfClient.get("jdbc"+key+".maxLifetime",ConfClient.get("spring.datasource"+key+".maxLifetime","1800000"));
		rtnMap.put("maxLifetime", maxLifetime);
		
		//maximumPoolSize
		String maximumPoolSize = ConfClient.get("jdbc"+key+".maximumPoolSize",ConfClient.get("spring.datasource"+key+".maximumPoolSize","10"));
		rtnMap.put("maximumPoolSize", maximumPoolSize);
		
		//minimumIdle
		String minimumIdle = ConfClient.get("jdbc"+key+".minimumIdle",ConfClient.get("spring.datasource"+key+".minimumIdle","2"));
		rtnMap.put("minimumIdle", minimumIdle);
        
		return rtnMap;
	}
	private static String getKey(String key) {
		if (StringUtil.isEmpty(key) || key.equals(DEAULT_DATASOUCE_KEY)) {
			return "";
		}
		return "."+key;
	}
}
