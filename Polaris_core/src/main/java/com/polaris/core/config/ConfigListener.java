package com.polaris.core.config;

import com.polaris.core.config.Config.Opt;

public interface ConfigListener {
	default void onChange(String sequence, Object key, Object value, Opt opt) {}
	default void onComplete(String sequence) {}
}
