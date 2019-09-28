package com.polaris.core;

import java.lang.reflect.Method;
import java.util.ServiceLoader;

public class ApplicationLauncher {
	
	private static final ServiceLoader<Launcher> launchers = ServiceLoader.load(Launcher.class);

	public static void main(String[] args) {
		for (Launcher launcher : launchers) {
			try {
				Method method = launcher.getClass().getMethod("main", String[].class);
				method.invoke(null, (Object)args);
			} catch (Exception e) {
				continue;
			}
    		break;
		}
	}
}
