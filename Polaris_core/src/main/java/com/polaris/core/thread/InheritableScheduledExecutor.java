package com.polaris.core.thread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;

/**
 * 支持可继承的线程变量的线程池（配合InheritableThreadLocal使用）
 */
public class InheritableScheduledExecutor extends NonInheritableScheduledExecutor {

	public InheritableScheduledExecutor(int corePoolSize,
	                                      String name) {
		super(corePoolSize, name);
	}

	public InheritableScheduledExecutor(int corePoolSize,
	                                      ThreadFactory threadFactory,
	                                      String name) {
	   super(corePoolSize, threadFactory, name);
	}
	public InheritableScheduledExecutor(int corePoolSize,
	                                     RejectedExecutionHandler handler,
	                                     String name) {
		super(corePoolSize, handler, name);
	}

	public InheritableScheduledExecutor(int corePoolSize,
	                                     ThreadFactory threadFactory,
	                                     RejectedExecutionHandler handler,
	                                     String name) {
		super(corePoolSize, threadFactory, handler, name);
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

    public String getName() {
        return name;
    }
}
