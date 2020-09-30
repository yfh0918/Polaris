package com.polaris.core.config.properties;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.core.config.Config.Opt;
import com.polaris.core.config.ConfigChangeListener;
import com.polaris.core.config.properties.ConfigurationProperties.ConfigurationPropertiesBean;
import com.polaris.core.util.SpringUtil;
import com.polaris.core.util.StringUtil;

import cn.hutool.core.collection.ConcurrentHashSet;

public class ConfigurationPropertiesEndPoint implements ConfigChangeListener{
	private Map<String, Set<ConfigurationPropertiesBean>> benMap = new ConcurrentHashMap<>();
	
	@Override
	public void onChange(String sequence, Object key, Object value, Opt opt) {
		ConfigurationProperties configurationProperties = SpringUtil.getBean(ConfigurationProperties.class);
		if (configurationProperties == null) {
			return;
		}
		Set<ConfigurationPropertiesBean> configBeans = configurationProperties.getConfigBeanSet();
		if (configBeans.size() == 0) {
			return;
		}
		Set<ConfigurationPropertiesBean> beanSet = benMap.get(sequence);
		if (beanSet == null) {
			synchronized(sequence.intern()) {
				if (beanSet == null) {
					beanSet = new ConcurrentHashSet<>();
					benMap.put(sequence, beanSet);
				}
			}
		}
		for (ConfigurationPropertiesBean bean : configBeans) {
			if (StringUtil.isEmpty(bean.annotation.prefix())) {
				beanSet.add(bean);
			} else {
				if (key.toString().startsWith(bean.annotation.prefix()+".")) {
					beanSet.add(bean);
				}
			}
		}
	}
	
	@Override
	public void onComplete(String sequence) {
		ConfigurationProperties configurationProperties = SpringUtil.getBean(ConfigurationProperties.class);
		if (configurationProperties == null) {
			return;
		}
		Set<ConfigurationPropertiesBean> beanSet = benMap.remove(sequence);
		if (beanSet != null) {
			for (ConfigurationPropertiesBean bean : beanSet) {
				if (bean.getAnnotation().autoRefreshed()) {
					configurationProperties.bind(bean.getObject(), bean.getAnnotation());
				}
			}
		}
		
	}
	
}
