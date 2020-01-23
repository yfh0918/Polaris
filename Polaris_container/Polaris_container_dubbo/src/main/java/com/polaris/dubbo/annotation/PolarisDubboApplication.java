package com.polaris.dubbo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.core.annotation.AliasFor;

import com.polaris.core.annotation.PolarisApplication;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@PolarisApplication
@EnableDubbo
public @interface PolarisDubboApplication {
	@AliasFor(annotation = PolarisApplication.class, attribute = "scanBasePackages")
	String[] scanBasePackages() default {};

	@AliasFor(annotation = PolarisApplication.class, attribute = "scanBasePackageClasses")
	Class<?>[] scanBasePackageClasses() default {};

	@AliasFor(annotation = PolarisApplication.class, attribute = "scanBasePackagesForMapper")
	String[] scanBasePackagesForMapper() default {};
	
    @AliasFor(annotation = EnableDubbo.class, attribute = "scanBasePackages")
    String[] scanBasePackagesForDubbo() default {};

    @AliasFor(annotation = EnableDubbo.class, attribute = "scanBasePackageClasses")
    Class<?>[] scanBasePackageClassesForDubbo() default {};

    @AliasFor(annotation = EnableDubbo.class, attribute = "multipleConfig")
    boolean multipleConfig() default true;
}