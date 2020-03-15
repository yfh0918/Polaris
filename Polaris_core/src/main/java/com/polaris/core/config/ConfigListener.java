package com.polaris.core.config;

import com.polaris.core.config.Config.Opt;

public interface ConfigListener {
	default boolean onChange(String sequence, Config config, String file, Object key, Object value, Opt opt) {return true;}
	default void onComplete(String sequence) {}
}
