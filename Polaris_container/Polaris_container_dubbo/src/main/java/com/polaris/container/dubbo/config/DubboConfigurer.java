package com.polaris.container.dubbo.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

import com.polaris.container.config.ConfigurationExtension;
import com.polaris.container.config.ConfigurationSupport;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.ReflectionUtil;
import com.polaris.core.util.StringUtil;

public class DubboConfigurer implements ConfigurationExtension{
	private static final Logger logger = LoggerFactory.getLogger(DubboConfigurer.class);
	
	@Override
	public Class<?>[] getExtensionConfigurations() {
		Class<?> clazz = DefaultDubboConfig.class;
		
		try {
			Map<String, Object> memberValues = 
					ReflectionUtil.getMemberValuesMap(clazz, EnableDubbo.class);
			
			//scanBasePackages
			String scanBasePackages = ConfClient.get("dubbo.scanBasePackages");
			if (StringUtil.isNotEmpty(scanBasePackages)) {
				memberValues.put("scanBasePackages", scanBasePackages.split(","));
			}
			
			//scanBasePackageClasses
			String scanBasePackageClasses = ConfClient.get("dubbo.scanBasePackageClasses");
			List<Class<?>> scanBasePackageClassList = new ArrayList<>();
			if (StringUtil.isNotEmpty(scanBasePackageClasses)) {
				for (String scanBasePackaeClassName : scanBasePackageClasses.split(",")) {
					try {
						scanBasePackageClassList.add(Class.forName(scanBasePackaeClassName));
					} catch (Exception ex) {
						logger.warn("Class:{} is not found", scanBasePackaeClassName);
					}
				}
			}
			if (scanBasePackageClassList.size() > 0) {
				Class<?>[] classArray = new Class[scanBasePackageClassList.size()];
				memberValues.put("scanBasePackageClasses", scanBasePackageClassList.toArray(classArray));
			} else {
				memberValues.put("scanBasePackageClasses", ConfigurationSupport.getClasses());
			}
			
			//multipleConfig
			memberValues.put("multipleConfig", Boolean.parseBoolean(ConfClient.get("dubbo.multipleConfig", "true")));
			
		} catch (Exception ex) {
			logger.error("ERROR", ex);
		}

		//返回
		return new Class<?>[]{clazz};
	}

	@EnableDubbo
	@Configuration
	protected static class DefaultDubboConfig {
		
		@Bean
	    public ApplicationConfig applicationConfig() {
	        ApplicationConfig applicationConfig = new ApplicationConfig();
	        ReflectionUtils.doWithMethods(ApplicationConfig.class, new MethodCallback() {
				@Override
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					ReflectionUtil.setMethodValueForSet(method, applicationConfig, "dubbo.application.");
				}
			});
	        applicationConfig.setName(ConfClient.getAppName());
	        applicationConfig.setQosEnable(Boolean.parseBoolean(ConfClient.get("dubbo.application.qosEnable", "false")));
	        return applicationConfig;
	    }
	    
	    @Bean
	    public RegistryConfig registryConfig() {
	        RegistryConfig registryConfig = new RegistryConfig();
	        ReflectionUtils.doWithMethods(RegistryConfig.class, new MethodCallback() {
				@Override
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					ReflectionUtil.setMethodValueForSet(method, registryConfig, "dubbo.registry.");
				}
			});
	        return registryConfig;
	    }
	    
	    @Bean
	    public ProtocolConfig protocolConfig() {
	    	ProtocolConfig protocolConfig = new ProtocolConfig();
	        ReflectionUtils.doWithMethods(ProtocolConfig.class, new MethodCallback() {
				@Override
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					ReflectionUtil.setMethodValueForSet(method, protocolConfig, "dubbo.protocol.");
				}
			});
	        return protocolConfig;
	    }
	    
	    @Bean    
	    public ProviderConfig providerConfig() {
	    	ProviderConfig providerConfig = new ProviderConfig();
	        ReflectionUtils.doWithMethods(ProviderConfig.class, new MethodCallback() {
				@Override
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					ReflectionUtil.setMethodValueForSet(method, providerConfig, "dubbo.provider.");
				}
			});
	    	return providerConfig;
	    }
	    

	}
}
