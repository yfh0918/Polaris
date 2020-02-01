package com.polaris.container.dubbo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.core.annotation.AliasFor;

import com.polaris.container.annotation.PolarisWebApplication;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@PolarisWebApplication
@EnableDubbo
public @interface PolarisDubboApplication {
	@AliasFor(annotation = PolarisWebApplication.class, attribute = "scanBasePackages")
	String[] scanBasePackages() default {};

	@AliasFor(annotation = PolarisWebApplication.class, attribute = "scanBasePackageClasses")
	Class<?>[] scanBasePackageClasses() default {};

	@AliasFor(annotation = PolarisWebApplication.class, attribute = "scanBasePackagesForMapper")
	String[] scanBasePackagesForMapper() default {};
	
    @AliasFor(annotation = EnableDubbo.class, attribute = "scanBasePackages")
    String[] scanBasePackagesForDubbo() default {};

    @AliasFor(annotation = EnableDubbo.class, attribute = "scanBasePackageClasses")
    Class<?>[] scanBasePackageClassesForDubbo() default {};

    @AliasFor(annotation = EnableDubbo.class, attribute = "multipleConfig")
    boolean multipleConfig() default true;
}