package com.polaris.comm.config;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.util.StringValueResolver;

import com.polaris.comm.Constant;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.config.EncryptPropertyPlaceholderConfigurer;
import com.polaris.comm.util.EncryptUtil;
import com.polaris.comm.util.LogUtil;

/**
 * rewrite PropertyPlaceholderConfigurer
 * @version 1.0
 *
 * <bean id="confPropertyPlaceholderConfigurer" class="com.polaris_conf_core.spring.ConfPropertyPlaceholderConfigurer" />
 *
 */
public class ConfPropertyPlaceholderConfigurer extends EncryptPropertyPlaceholderConfigurer {
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
					
					//获取应用名称
					if (Constant.PROJECT_NAME.equals(key)) {
						zkValue = ConfClient.getAppName();
					
					//DUBBO端口号
					} else if (Constant.PORT_NAME.equals(key)) {
						zkValue = Constant.PORT;
						
					//其他参数
					} else {
						zkValue = ConfClient.get(key,false);//启动参数无需wartch
					}
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

		// init bean define visitor
		BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);

		// visit bean definition
		String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
		if (beanNames != null && beanNames.length > 0) {
			for (String beanName : beanNames) {
				if (!(beanName.equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {
					BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(beanName);
					visitor.visitBeanDefinition(bd);
				}
			}
		}
		
		//调用加密算法
		super.processProperties(beanFactoryToProcess, props);
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	private String beanName;
	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	private BeanFactory beanFactory;
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
		super.setIgnoreUnresolvablePlaceholders(true);
	}

}
