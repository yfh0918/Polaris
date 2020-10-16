package com.polaris.core.config.provider;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.config.ConfigChangeListener;
import com.polaris.core.config.provider.Config.Opt;
import com.polaris.core.util.UuidUtil;

@SuppressWarnings("rawtypes")
public class ConfigChangeNotifier {
	private static final Logger logger = LoggerFactory.getLogger(ConfigChangeNotifier.class);
	public static final ConfigChangeNotifier INSTANCE = new ConfigChangeNotifier();
	private ConfigChangeNotifier() {}
	
	public void notify(String group, String file, Properties oldProperties, Properties newProperties, ConfigChangeListener... configListeners) {
		
		//generate id for one notify
		String sequence = UuidUtil.generateUuid();
		
		//start
		if (configListeners != null) {
            for (ConfigChangeListener configListener : configListeners) {
                configListener.onStart(sequence);
            }
        }
		
		//add or update or delete
		for (Map.Entry entry : newProperties.entrySet()) {
			if (oldProperties == null || !oldProperties.containsKey(entry.getKey())) {
                if (configListeners != null) {
                    for (ConfigChangeListener configListener : configListeners) {
                        configListener.onChange(sequence, group, file, entry.getKey(), entry.getValue(), Opt.ADD);
                    }
                }
                if (oldProperties != null) {
                    logger.info("group:{} file:{} key:{} newValue:{} opt:{}", group,file,entry.getKey(),entry.getValue(),Opt.ADD.name());
                }
			} else if (!Objects.equals(oldProperties.get(entry.getKey()), newProperties.get(entry.getKey()))) {
                if (configListeners != null) {
                    for (ConfigChangeListener configListener : configListeners) {
                        configListener.onChange(sequence, group, file,entry.getKey(), entry.getValue(), Opt.UPD);
                    }
                }
                logger.info("group:{} file:{} key:{} oldValue:{} newvalue:{} opt:{}", group,file,entry.getKey(),oldProperties.get(entry.getKey()), entry.getValue(),Opt.UPD.name());
			}
			if (oldProperties != null) {
				oldProperties.remove(entry.getKey());
			}
		}
		if (oldProperties != null) {
			for (Map.Entry entry : oldProperties.entrySet()) {
                if (configListeners != null) {
                    for (ConfigChangeListener configListener : configListeners) {
                        configListener.onChange(sequence, group, file,entry.getKey(), entry.getValue(), Opt.DEL);
                    }
                }
                logger.info("group:{} file:{}, key:{} value:{} opt:{}", group,file,entry.getKey(),entry.getValue(),Opt.DEL.name());
			}
		}
		
		//complete
		if (configListeners != null) {
            for (ConfigChangeListener configListener : configListeners) {
                configListener.onComplete(sequence);
            }
        }
	}
}
