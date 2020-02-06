package com.polaris.database.config;


import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Joiner;
import com.polaris.container.config.ConfigurationSupport;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.StringUtil;
import com.polaris.database.PolarisBaseMapper;

import tk.mybatis.spring.mapper.MapperScannerConfigurer;
 
/**
 * @Auther: yufenghua
 * @Date: 2019/9/11 09:27
 * @Description:
 */
@Configuration
public class MybatisConfigurer{
	
    @Bean
    public static MapperScannerConfigurer mapperScannerConfigurer() {
    	
		String mapperScanBasePackage = ConfClient.get("jdbc.mapperScanner.basePackage",ConfClient.get("spring.datasource.mapperScanner.basePackage"));
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        
        //Mapper接口目录，具体的mapper
        if (StringUtil.isNotEmpty(mapperScanBasePackage)) {
            mapperScannerConfigurer.setBasePackage(mapperScanBasePackage);
        } else {
            mapperScannerConfigurer.setBasePackage(Joiner.on(',').skipNulls().join(ConfigurationSupport.getDefaultBasePackagesForMapper()));
        }
 
        //配置通用Mapper，详情请查阅官方文档
        Properties properties = new Properties();
        properties.setProperty("mappers", PolarisBaseMapper.class.getName());
        properties.setProperty("notEmpty", "false");//insert、update是否判断字符串类型!='' 即 test="str != null"表达式内是否追加 and str != ''
        properties.setProperty("IDENTITY", "MYSQL");
        mapperScannerConfigurer.setProperties(properties);
 
        return mapperScannerConfigurer;
    }
}

