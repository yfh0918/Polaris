package com.polaris.core.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 支持可继承的线程变量的线程池（配合InheritableThreadLocal使用）
 */
public class InheritableExecutor extends NonInheritableExecutor {


	public InheritableExecutor(int corePoolSize,
	                                      int maximumPoolSize,
	                                      long keepAliveTime,
	                                      TimeUnit unit,
	                                      BlockingQueue<Runnable> workQueue,
	                                      String name) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,name);
	}

	public InheritableExecutor(int corePoolSize,
	                                      int maximumPoolSize,
	                                      long keepAliveTime,
	                                      TimeUnit unit,
	                                      BlockingQueue<Runnable> workQueue,
	                                      ThreadFactory threadFactory,
	                                      String name) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,name);
	}
	public InheritableExecutor(int corePoolSize,
	                                     int maximumPoolSize,
	                                     long keepAliveTime,
	                                     TimeUnit unit,
	                                     BlockingQueue<Runnable> workQueue,
	                                     RejectedExecutionHandler handler,
	                                     String name) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler,name);
	}

	public InheritableExecutor(int corePoolSize,
	                                     int maximumPoolSize,
	                                     long keepAliveTime,
	                                     TimeUnit unit,
	                                     BlockingQueue<Runnable> workQueue,
	                                     ThreadFactory threadFactory,
	                                     RejectedExecutionHandler handler,
	                                     String name) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler,name);
	}

	/**
	 * 重写执行线程实例的方法
	 * @param command
	 */
	@Override
	public void execute(Runnable command) {
		if (command == null){
			throw new NullPointerException();
		}
		TaskWithThreadLocal task =new TaskWithThreadLocal(command,PolarisInheritableThreadLocal.getThreadLocalsMap());
		super.execute(task);
	}
}
