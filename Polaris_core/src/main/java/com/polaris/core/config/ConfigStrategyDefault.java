package com.polaris.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.config.Config.Opt;

public class ConfigStrategyDefault implements ConfigStrategy {
	private static final Logger logger = LoggerFactory.getLogger(ConfigStrategyDefault.class);
	public static final ConfigStrategy INSTANCE = new ConfigStrategyDefault();
	private ConfigStrategyDefault() {}
	
	@Override
	public boolean canChange(Config config,String file, Object key, Object value,Opt opt) {
		//优先级-ext
		if (config == ConfigFactory.get(Config.EXT)) {
			if (ConfigFactory.get(Config.SYSTEM).contain(key)) {
				logger.warn("type:{} file:{}, key:{} value:{} opt:{} failed ,"
						+ "caused by conflicted with system properties ", config.getType(),file,key,value,opt.name());
				return false;
			}
		}
		
		//优先级-global
		if (config == ConfigFactory.get(Config.GLOBAL)) {
			if (ConfigFactory.get(Config.SYSTEM).contain(key)) {
				logger.warn("type:{} file:{}, key:{} value:{} opt:{} failed ,"
						+ "caused by conflicted with system properties", config.getType(),file,key,value,opt.name());
				return false;
			}
			if (ConfigFactory.get(Config.EXT).contain(key)) {
				logger.warn("type:{} file:{}, key:{} value:{} opt:{} failed ,"
						+ "caused by conflicted with ext properties", config.getType(),file,key,value,opt.name());
				return false;
			}
		}
		
		return true;
	}
}
