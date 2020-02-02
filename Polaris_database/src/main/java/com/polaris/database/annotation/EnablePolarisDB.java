package com.polaris.database.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.polaris.database.config.DataSourceConfig;
import com.polaris.database.config.MybatisConfigurer;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({DataSourceConfig.class,MybatisConfigurer.class})
@EnableTransactionManagement
public @interface EnablePolarisDB {
	@AliasFor(annotation = EnableTransactionManagement.class, attribute = "proxyTargetClass")
	boolean proxyTargetClass() default true;
	
	@AliasFor(annotation = EnableTransactionManagement.class, attribute = "mode")
	AdviceMode mode() default AdviceMode.PROXY;
}
