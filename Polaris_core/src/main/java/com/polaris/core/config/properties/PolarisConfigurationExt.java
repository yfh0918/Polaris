package com.polaris.core.config.properties;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * An annotation as Properties files.
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@Import(ConfigurationExtImport.class)
public @interface PolarisConfigurationExt {

	/**
	 * extension file from local or data center
	 */
	String[] value();


}
