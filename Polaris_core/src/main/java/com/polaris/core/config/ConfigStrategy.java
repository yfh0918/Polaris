package com.polaris.core.config;

import com.polaris.core.config.Config.Opt;

public interface ConfigStrategy {
	
	boolean canChange(Config config,String file, Object key, Object value,Opt opt) ;
}
