package com.polaris.core.thread;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NonInheritableScheduledExecutor extends ScheduledThreadPoolExecutor implements NamingExecutor{
	private static Logger logger = LoggerFactory.getLogger(NonInheritableScheduledExecutor.class);

	protected final String  name;

	public NonInheritableScheduledExecutor(int corePoolSize,
	                                      String name) {
		super(corePoolSize);
        this.name = name;
	}

	public NonInheritableScheduledExecutor(int corePoolSize,
	                                      ThreadFactory threadFactory,
	                                      String name) {
	   super(corePoolSize, threadFactory);
	   this.name = name;
	}
	public NonInheritableScheduledExecutor(int corePoolSize,
	                                     RejectedExecutionHandler handler,
	                                     String name) {
		super(corePoolSize, handler);
        this.name = name;
	}

	public NonInheritableScheduledExecutor(int corePoolSize,
	                                     ThreadFactory threadFactory,
	                                     RejectedExecutionHandler handler,
	                                     String name) {
		super(corePoolSize, threadFactory, handler);
		this.name = name;
	}

	@Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            try {
                final Future<?> f = (Future<?>) r;
                if (f.isDone()) {
                    f.get();
                }
            } catch (final CancellationException ce) {
                // ignored
            } catch (final ExecutionException ee) {
                t = ee.getCause();
            } catch (final InterruptedException ie) {
                Thread.currentThread().interrupt(); // ignore/reset
            }
        }
        if (t != null) {
        	logger.error("Uncaught exception in pool: {}, {}.", this.name, super.toString(), t);
        }
    }
	
	@Override
    public String getName() {
        return name;
    }
}
