package com.polaris.container;

import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.container.listener.ServerListenerHelper;
import com.polaris.core.component.AbstractLifeCycleWithListener;
import com.polaris.core.component.LifeCycle;
import com.polaris.core.util.SpringUtil;

public class ServerManager extends AbstractLifeCycleWithListener {

	/**
     * constructor ServerManager for private 
     * 
     */
	private static ServerManager INSTANCE = new ServerManager();
	private ServerManager() {
	}
	
	public static void init() {
		INSTANCE.start();
	}
	
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
    
	protected void doStart() throws Exception {
		ServerProvider.getServer().start();
    }

    protected void doStop() throws Exception {
    	ServerProvider.getServer().stop();
    }
    
	@Override
	public void lifeCycleStarting(LifeCycle event) {
		ServerListenerHelper.starting();
	}

	@Override
	public void lifeCycleStarted(LifeCycle event) {
		ServerListenerHelper.started();
        Runtime.getRuntime().addShutdownHook(jvmShutdownHook);
        ConfigurableApplicationContext context = SpringUtil.getApplicationContext();
        if (context != null) {
        	context.registerShutdownHook();
        }
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
}
