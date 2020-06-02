package com.polaris.container;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.container.listener.ServerListenerHelper;
import com.polaris.core.OrderWrapper;
import com.polaris.core.component.LifeCycle;
import com.polaris.core.component.LifeCycleListener;
import com.polaris.core.component.LifeCycleWithListenerManager;
import com.polaris.core.util.SpringUtil;

public class ServerManager extends LifeCycleWithListenerManager implements LifeCycleListener{

	/**
     * constructor ServerManager for private 
     * 
     */
	private static ServerManager INSTANCE = new ServerManager();
	private ServerManager() {
		addLifeCycleListener(this);
	}
	
	public static void init() throws Exception {
		INSTANCE.start();
	}
	
	/**
     * JVM shutdown hook to shutdown this server. Declared as a class-level variable to allow removing the shutdown hook when the
     * server is stopped normally.
     */
    private final Thread jvmShutdownHook = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
				stop();
			} catch (Exception e) {
				//ignore
			}
        }
    }, "ServerContainer-JVM-shutdown-hook");
    
    @Override
	protected void doStart() throws Exception {
		ServerProvider.getServer().start();
    }
    
    @Override
    protected void doStop() throws Exception {
    	ServerProvider.getServer().stop();
    }
    
    public Object getContext() {
    	return ServerProvider.getServer().getContext();
    }
    
	@Override
	public void starting(LifeCycle event) {
		ServerListenerHelper.starting(event);
	}

	@Override
	public void started(LifeCycle event) {
		ServerListenerHelper.started(event);
        Runtime.getRuntime().addShutdownHook(jvmShutdownHook);
        ConfigurableApplicationContext context = SpringUtil.getApplicationContext();
        if (context != null) {
        	context.registerShutdownHook();
        }
	}

	@Override
	public void failure(LifeCycle event, Throwable cause) {
		ServerListenerHelper.failure(event,cause);
	}

	@Override
	public void stopping(LifeCycle event) {
		ServerListenerHelper.stopping(event);
		//lifeCycleStopped method will not be called after the service is stopped
		//so stopped method is called after the service is stopping
		ServerListenerHelper.stopped(event);
	}
	
	static private class ServerProvider {
		private static final ServiceLoader<Server> servers = ServiceLoader.load(Server.class);
		private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
		@SuppressWarnings("rawtypes")
		private static List<OrderWrapper> serverList = new ArrayList<OrderWrapper>();
	    private static volatile Server server;
		private ServerProvider() {}
		
	    public static Server getServer() {
	    	if (initialized.compareAndSet(false, true)) {
	    		for (Server server : servers) {
	        		OrderWrapper.insertSorted(serverList, server);
	            }
	        	if (serverList.size() > 0) {
	        		server = (Server)serverList.get(0).getHandler();
	        	}
	        }
	    	if (server == null) {
	    		throw new RuntimeException("Polaris_container_xxx is not found, please check the pom.xml");
	    	}
	    	return server;
	    }
	}
}
