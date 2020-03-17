package com.polaris.core.config;

public interface ConfigStrategy {
	void notify(ConfigListener configListener, Config config,String file, String contents);
}
