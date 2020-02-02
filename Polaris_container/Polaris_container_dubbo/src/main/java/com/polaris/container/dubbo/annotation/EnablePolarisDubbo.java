package com.polaris.container.dubbo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import com.polaris.container.dubbo.config.DefaultDubboConfig;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DefaultDubboConfig.class)
@EnableDubbo
public @interface EnablePolarisDubbo {
    @AliasFor(annotation = EnableDubbo.class, attribute = "scanBasePackages")
    String[] scanBasePackages() default {};

    @AliasFor(annotation = EnableDubbo.class, attribute = "scanBasePackageClasses")
    Class<?>[] scanBasePackageClasses() default {};

    @AliasFor(annotation = EnableDubbo.class, attribute = "multipleConfig")
    boolean multipleConfig() default true;

}
