package com.polaris.core.naming.provider;

import com.polaris.core.naming.NamingUrlHandler;

public class NamingUrlHandlerFactory {
	private static NamingUrlHandler handler = NamingUrlHandlerDefault.INSTANCE;

	public static NamingUrlHandler get() {
		return handler;
	}
	public static void set(NamingUrlHandler namingHandler) {
	    handler = namingHandler;
	}
}
