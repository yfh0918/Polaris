package com.polaris.comm.config;

import java.util.Properties;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.polaris.comm.util.EncryptUtil;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.StringUtil;


public class EncryptPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer{  
    private static final LogUtil logger = LogUtil.getInstance(EncryptPropertyPlaceholderConfigurer.class, false);

    private void dealProp(String alias, String password, String key, Properties props) {
    	try {
			EncryptUtil encrypt;
			if (StringUtil.isEmpty(alias)) {
				encrypt = EncryptUtil.getInstance();
			} else {
				encrypt = new EncryptUtil(alias);
			}
	    	password = encrypt.decrypt(EncryptUtil.START_WITH,password);
			props.setProperty(key, password);
		} catch (Exception e) {
			logger.error("解密 失败： ",e);
		}
	}

    @Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
    	String alias = props.getProperty("db.password.alias");
		Set<?> keys = props.keySet();
    	for (Object obj : keys) {
    		String key = obj.toString();
    		String value = props.getProperty(key);
    		dealProp(alias, value, key, props);
    	}
    	
		super.processProperties(beanFactory, props);
    }
    

}  
