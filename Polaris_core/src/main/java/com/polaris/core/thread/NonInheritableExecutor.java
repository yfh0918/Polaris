package com.polaris.core.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.component.Naming;

/**
 * 支持可继承的线程变量的线程池（配合InheritableThreadLocal使用）
 */
public class NonInheritableExecutor extends ThreadPoolExecutor implements Naming {
	private static Logger logger = LoggerFactory.getLogger(NonInheritableExecutor.class);

	protected final String  name;

	public NonInheritableExecutor(int corePoolSize,
	                                      int maximumPoolSize,
	                                      long keepAliveTime,
	                                      TimeUnit unit,
	                                      BlockingQueue<Runnable> workQueue,
	                                      String name) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		this.name = name;
	}

	public NonInheritableExecutor(int corePoolSize,
	                                      int maximumPoolSize,
	                                      long keepAliveTime,
	                                      TimeUnit unit,
	                                      BlockingQueue<Runnable> workQueue,
	                                      ThreadFactory threadFactory,
	                                      String name) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		this.name = name;
	}
	public NonInheritableExecutor(int corePoolSize,
	                                     int maximumPoolSize,
	                                     long keepAliveTime,
	                                     TimeUnit unit,
	                                     BlockingQueue<Runnable> workQueue,
	                                     RejectedExecutionHandler handler,
	                                     String name) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		this.name = name;
	}

	public NonInheritableExecutor(int corePoolSize,
	                                     int maximumPoolSize,
	                                     long keepAliveTime,
	                                     TimeUnit unit,
	                                     BlockingQueue<Runnable> workQueue,
	                                     ThreadFactory threadFactory,
	                                     RejectedExecutionHandler handler,
	                                     String name) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
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
