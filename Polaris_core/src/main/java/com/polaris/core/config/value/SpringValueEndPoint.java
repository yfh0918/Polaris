package com.polaris.core.config.value;

import com.polaris.core.config.ConfEndPoint;
import com.polaris.core.config.Config.Opt;
import com.polaris.core.util.SpringUtil;

public class SpringValueEndPoint implements ConfEndPoint{
	@Override
	public void onChange(String key, String value, Opt opt) {
		if (opt == Opt.DELETE) {
			return;
		}
		SpringAutoUpdateConfigChangeListener listener = SpringUtil.getBean(SpringAutoUpdateConfigChangeListener.class);
		if (listener != null) {
			listener.onChange(key.toString());
		}
	}
}
