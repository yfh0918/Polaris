package com.polaris.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@PolarisBaseApplication
@EnableTransactionManagement(proxyTargetClass=true)
public @interface PolarisApplication {
	@AliasFor(annotation = PolarisBaseApplication.class, attribute = "scanBasePackages")
	String[] scanBasePackages() default {};

	@AliasFor(annotation = PolarisBaseApplication.class, attribute = "scanBasePackageClasses")
	Class<?>[] scanBasePackageClasses() default {};

	String[] scanBasePackagesForMapper() default {};
}
