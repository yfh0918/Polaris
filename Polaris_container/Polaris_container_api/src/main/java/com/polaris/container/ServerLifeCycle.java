package com.polaris.container;

import com.polaris.container.listener.ServerListenerHelper;
import com.polaris.core.component.AbstractLifeCycle;
import com.polaris.core.component.LifeCycle;

public class ServerLifeCycle extends AbstractLifeCycle implements LifeCycle.Listener{

	public static ServerLifeCycle INSTANCE = new ServerLifeCycle();
	
	/**
     * JVM shutdown hook to shutdown this server. Declared as a class-level variable to allow removing the shutdown hook when the
     * server is stopped normally.
     */
    private final Thread jvmShutdownHook = new Thread(new Runnable() {
        @Override
        public void run() {
            stop();
        }
    }, "ServerContainer-JVM-shutdown-hook");
    
	private ServerLifeCycle () {
		addLifeCycleListener(this);
	}
	
	protected void doStart() throws Exception {
		ServerFactory.getServer().start();
    }

    protected void doStop() throws Exception {
    	ServerFactory.getServer().stop();
    }
    
	@Override
	public void lifeCycleStarting(LifeCycle event) {
		ServerListenerHelper.starting();
	}

	@Override
	public void lifeCycleStarted(LifeCycle event) {
		ServerListenerHelper.started();
        Runtime.getRuntime().addShutdownHook(jvmShutdownHook);
	}

	@Override
	public void lifeCycleFailure(LifeCycle event, Throwable cause) {
		ServerListenerHelper.failure();
	}

	@Override
	public void lifeCycleStopping(LifeCycle event) {
		ServerListenerHelper.stopping();
		//lifeCycleStopped method will not be called after the service is stopped
		//so stopped method is called after the service is stopping
		ServerListenerHelper.stopped();
	}

	@Override
	public void lifeCycleStopped(LifeCycle event) {
		//lifeCycleStopped method will not be called after the service is stopped
	}

}
