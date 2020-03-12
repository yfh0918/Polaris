package com.polaris.core.config;

import com.polaris.core.config.Config.Opt;

public interface ConfEndPoint {
	default void init() {};
	default void onChange(String key, String value, Opt opt) {};
}
