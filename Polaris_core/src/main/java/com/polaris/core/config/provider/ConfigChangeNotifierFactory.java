package com.polaris.core.config.provider;

import com.polaris.core.config.ConfigChangeNotifier;

public abstract class ConfigChangeNotifierFactory {
	
	private static ConfigChangeNotifier notifier = ConfigChangeNotifierDefault.INSTANCE;

	public static ConfigChangeNotifier get() {
		return notifier;
	}
	public static void set(ConfigChangeNotifier notifierImpl) {
	    notifier = notifierImpl;
	}
}
