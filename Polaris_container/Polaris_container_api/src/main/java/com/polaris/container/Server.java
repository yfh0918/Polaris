package com.polaris.container;

public interface Server {
	void start() throws Exception;
	default Object getContext() {return null;}
	default void stop() throws Exception {}
}
