package com.polaris.container.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@PolarisApplication
@EnableTransactionManagement(proxyTargetClass=true)
@EnableAspectJAutoProxy(proxyTargetClass=true)
public @interface PolarisWebApplication {
	@AliasFor(annotation = PolarisApplication.class, attribute = "scanBasePackages")
	String[] scanBasePackages() default {};

	@AliasFor(annotation = PolarisApplication.class, attribute = "scanBasePackageClasses")
	Class<?>[] scanBasePackageClasses() default {};

	String[] scanBasePackagesForMapper() default {};
}
