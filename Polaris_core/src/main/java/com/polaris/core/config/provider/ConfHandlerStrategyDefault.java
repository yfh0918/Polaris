package com.polaris.core.config.provider;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.config.Config;
import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.ConfigListener;
import com.polaris.core.config.Config.Opt;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.reader.ConfReaderFactory;
import com.polaris.core.util.UuidUtil;

@SuppressWarnings("rawtypes")
public class ConfHandlerStrategyDefault implements ConfHandlerStrategy {
	private static final Logger logger = LoggerFactory.getLogger(ConfHandlerStrategyDefault.class);
	public static final ConfHandlerStrategy INSTANCE = new ConfHandlerStrategyDefault();
	private ConfHandlerStrategyDefault() {}
	
	@Override
	public void notify(ConfigListener configListener, Config config, String file, String contents) {
		Properties oldProperties = config.getProperties(file);
		Properties newProperties = ConfReaderFactory.get(file).getProperties(contents);
		String sequence = UuidUtil.generateUuid();
		boolean isUpdate = false;
		for (Map.Entry entry : newProperties.entrySet()) {
			if (oldProperties == null || !oldProperties.containsKey(entry.getKey())) {
				if (canUpdate(sequence, config, file, entry.getKey(), entry.getValue(), Opt.ADD)) {
					isUpdate = true;
					configListener.onChange(sequence, entry.getKey(), entry.getValue(), Opt.ADD);
					if (oldProperties != null) {
						logger.info("type:{} file:{} key:{} newValue:{} opt:{}", config.getType(),file,entry.getKey(),entry.getValue(),Opt.ADD.name());
					}
				}
			} else if (!Objects.equals(oldProperties.get(entry.getKey()), newProperties.get(entry.getKey()))) {
				if (canUpdate(sequence, config, file, entry.getKey(), entry.getValue(), Opt.UPDATE)) {
					isUpdate = true;
					configListener.onChange(sequence, entry.getKey(), entry.getValue(), Opt.UPDATE);
					logger.info("type:{} file:{} key:{} oldValue:{} newvalue:{} opt:{}", config.getType(),file,entry.getKey(),oldProperties.get(entry.getKey()), entry.getValue(),Opt.UPDATE.name());
				}
			}
			if (oldProperties != null) {
				oldProperties.remove(entry.getKey());
			}
		}
		if (oldProperties != null) {
			for (Map.Entry entry : oldProperties.entrySet()) {
				if (canUpdate(sequence, config, file, entry.getKey(), entry.getValue(), Opt.DELETE)) {
					isUpdate = true;
					configListener.onChange(sequence, entry.getKey(), entry.getValue(), Opt.DELETE);
					logger.info("type:{} file:{}, key:{} value:{} opt:{}", config.getType(),file,entry.getKey(),entry.getValue(),Opt.DELETE.name());
				}
			}
		}
		config.put(file, newProperties);
		if (isUpdate) {
			configListener.onComplete(sequence);
		}
	}
	
	public boolean canUpdate(String sequence, Config config,String file, Object key, Object value,Opt opt) {
		//优先级-ext
		if (config == ConfigFactory.get(Type.EXT)) {
			if (ConfigFactory.get(Type.SYS).contain(key)) {
				logger.warn("type:{} file:{}, key:{} value:{} opt:{} failed ,"
						+ "caused by conflicted with system properties ", config.getType(),file,key,value,opt.name());
				return false;
			}
		}
		
		//优先级-global
		if (config == ConfigFactory.get(Type.GBL)) {
			if (ConfigFactory.get(Type.SYS).contain(key)) {
				logger.warn("type:{} file:{}, key:{} value:{} opt:{} failed ,"
						+ "caused by conflicted with system properties", config.getType(),file,key,value,opt.name());
				return false;
			}
			if (ConfigFactory.get(Type.EXT).contain(key)) {
				logger.warn("type:{} file:{}, key:{} value:{} opt:{} failed ,"
						+ "caused by conflicted with ext properties", config.getType(),file,key,value,opt.name());
				return false;
			}
		}
		return true;
	}
	

}
