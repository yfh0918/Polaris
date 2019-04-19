package com.polaris.comm.config;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.util.StringValueResolver;

import com.polaris.comm.util.EncryptUtil;
import com.polaris.comm.util.LogUtil;

/**
 * rewrite PropertyPlaceholderConfigurer
 * @version 1.0
 *
 * <bean id="confPropertyPlaceholderConfigurer" class="com.polaris_conf_core.spring.ConfPropertyPlaceholderConfigurer" />
 *
 */
public class ConfPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	private static final LogUtil logger = LogUtil.getInstance(ConfPropertyPlaceholderConfigurer.class);

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {

		// init value resolver
		StringValueResolver valueResolver = new StringValueResolver() {
			String placeholderPrefix = "${";
			String placeholderSuffix = "}";
			@Override
			public String resolveStringValue(String strVal) {
				StringBuilder buf = new StringBuilder(strVal);
				
				// loop replace by polaris_conf, if the value match '${***}'
				boolean start = strVal.contains(placeholderPrefix);
				boolean end = strVal.contains(placeholderSuffix);
				
				while (start && end) {
					String zkValue = null;
					int startIndex = strVal.indexOf(placeholderPrefix) + placeholderPrefix.length();
					int endIndex = strVal.indexOf(placeholderSuffix);
					String key = buf.substring(startIndex, endIndex);
					
					String startValue = buf.substring(0, startIndex - placeholderPrefix.length());
					String endValue = buf.substring(endIndex + placeholderSuffix.length());
					zkValue = ConfClient.get(key,false);//启动参数无需wartch
					buf = new StringBuilder();
					buf.append(startValue);
					logger.info(">>>>>>>>>>> polaris_conf resolved placeholder '" + key + "' to value [" + zkValue + "]");
					//解密操作
					try {
						EncryptUtil encrypt = EncryptUtil.getInstance();
						zkValue = encrypt.decrypt(EncryptUtil.START_WITH, zkValue);
					} catch (Exception ex) {
						//nothing
					}
					buf.append(zkValue);
					buf.append(endValue);
					start = buf.toString().contains(placeholderPrefix);
					end = buf.toString().contains(placeholderSuffix);
				}
				
				return buf.toString();
				
			}
		};

		super.doProcessProperties(beanFactoryToProcess, valueResolver);
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}


	@Override
	public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
		super.setIgnoreUnresolvablePlaceholders(true);
	}

}
