package com.polaris.core.config;

import com.polaris.core.config.Config.Opt;

public interface ConfigListener {
	boolean onChange(String sequence, Config config, String file, Object key, Object value, Opt opt);
	void onComplete(String sequence);
}
