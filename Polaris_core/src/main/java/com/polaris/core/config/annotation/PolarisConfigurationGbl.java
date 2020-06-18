package com.polaris.core.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.polaris.core.config.properties.ConfigurationGblImport;

/**
 * An annotation as Properties files.
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@Import(ConfigurationGblImport.class)
public @interface PolarisConfigurationGbl {

	/**
	 * global file from local or data center
	 */
	String[] value() default{};
}
