package com.polaris.core.config.properties;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

/**
 * An annotation for Polaris configuration Properties for binding POJO as Properties Object.
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@PolarisConfigurationExt
@PolarisConfigurationGlobal
@Import(ConfigurationPropertiesImport.class)
public @interface PolarisConfigurationProperties {

	/**
	 * The name prefix of the properties that are valid to bind to this object. Synonym
	 * for {@link #prefix()}. A valid prefix is defined by one or more words separated
	 * with dots (e.g. {@code "acme.system.feature"}).
	 * @return the name prefix of the properties to bind
	 */
	@AliasFor("prefix")
	String value() default "";

	/**
	 * The name prefix of the properties that are valid to bind to this object. Synonym
	 * for {@link #value()}. A valid prefix is defined by one or more words separated with
	 * dots (e.g. {@code "acme.system.feature"}).
	 * @return the name prefix of the properties to bind
	 */
	@AliasFor("value")
	String prefix() default "";
    
	
    /**
     * It indicates the properties of current doBind bean is auto-refreshed when configuration is changed.
     *
     * @return default value is <code>false</code>
     */
    boolean autoRefreshed() default true;
    
    /**
     * linked PolarisConfigurationExt
     *
     * @return default value is <code>ext</code>
     */
	@AliasFor(annotation = PolarisConfigurationExt.class, attribute = "value")
	String[] ext() default {};
	
	/**
     * linked PolarisConfigurationGlobal
     *
     * @return default value is <code>global</code>
     */
	@AliasFor(annotation = PolarisConfigurationGlobal.class, attribute = "value")
	String[] global() default {};
}
