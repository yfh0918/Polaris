package com.polaris.core.config;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.config.Config.Opt;

import cn.hutool.core.collection.ConcurrentHashSet;

public class ConfigStrategyDefault implements ConfigStrategy {
	private static final Logger logger = LoggerFactory.getLogger(ConfigStrategyDefault.class);
	public static final ConfigStrategy INSTANCE = new ConfigStrategyDefault();
	private ConfigListener configListener;
	private Set<String> sequenceSet = new ConcurrentHashSet<>();
	private ConfigStrategyDefault() {}
	
	@Override
	public void init(ConfigListener configListener) {
		this.configListener = configListener;
	}
	
	@Override
	public boolean onChange(String sequence, Config config,String file, Object key, Object value,Opt opt) {
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
		configListener.onChange(sequence, key, value, opt);
		if (!sequenceSet.contains(sequence)) {
			sequenceSet.add(sequence);
		}
		return true;
	}
	
	@Override
	public void onComplete(String sequence) {
		if (sequenceSet.remove(sequence)) {
			configListener.onComplete(sequence);
		}
	}
}
