package com.polaris.container.springboot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.AliasFor;

import com.polaris.container.annotation.PolarisApplication;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@PolarisApplication
@SpringBootApplication
public @interface PolarisSpringbootApplication {
	
	@AliasFor(annotation = SpringBootApplication.class, attribute = "exclude")
	Class<?>[] exclude() default {};

	@AliasFor(annotation = SpringBootApplication.class, attribute = "excludeName")
	String[] excludeName() default {};


	@AliasFor(annotation = SpringBootApplication.class, attribute = "scanBasePackages")
	String[] scanBasePackages() default {};


	@AliasFor(annotation = SpringBootApplication.class, attribute = "scanBasePackageClasses")
	Class<?>[] scanBasePackageClasses() default {};


	@AliasFor(annotation = SpringBootApplication.class, attribute = "proxyBeanMethods")
	boolean proxyBeanMethods() default true;
	
}