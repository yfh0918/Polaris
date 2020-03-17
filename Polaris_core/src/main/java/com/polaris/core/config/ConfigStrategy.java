package com.polaris.core.config;

import com.polaris.core.config.Config.Opt;

public interface ConfigStrategy {
	void init(ConfigListener configListener);
	boolean onChange(String sequence, Config config,String file, Object key, Object value,Opt opt) ;
	void onComplete(String sequence) ;
}
