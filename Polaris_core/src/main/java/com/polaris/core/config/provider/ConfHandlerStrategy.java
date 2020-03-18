package com.polaris.core.config.provider;

import com.polaris.core.config.Config;
import com.polaris.core.config.ConfigListener;

public interface ConfHandlerStrategy {
	void notify(ConfigListener configListener, Config config,String file, String contents);
}
