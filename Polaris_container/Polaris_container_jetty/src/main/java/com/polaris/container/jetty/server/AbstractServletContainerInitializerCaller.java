package com.polaris.container.jetty.server;

import org.eclipse.jetty.servlet.ServletContextHandler.ServletContainerInitializerCaller;

public class AbstractServletContainerInitializerCaller implements ServletContainerInitializerCaller {

	@Override
	public void start() throws Exception {
	}

	@Override
	public void stop() throws Exception {
	}

	@Override
	public boolean isRunning() {
		return false;
	}

	@Override
	public boolean isStarted() {
		return false;
	}

	@Override
	public boolean isStarting() {
		return false;
	}

	@Override
	public boolean isStopping() {
		return false;
	}

	@Override
	public boolean isStopped() {
		return false;
	}

	@Override
	public boolean isFailed() {
		return false;
	}

	@Override
	public void addLifeCycleListener(Listener listener) {
	}

	@Override
	public void removeLifeCycleListener(Listener listener) {
	}

}
