package com.polaris.container;

public interface Server {
	public void start();
	public default void stop() {}
	public default Object getContext() {return null;}
}
