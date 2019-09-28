package com.polaris.dubbo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

abstract public class DubboApplication {

	public static void loadContext(Class<?>... annotatedClasses) {
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(annotatedClasses);
		applicationContext.refresh();
	} 
}
