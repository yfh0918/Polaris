package com.polaris.core.thread;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.util.Requires;

/**
 * called by ThreadPoolListerner of ServerListenerHelper
 */
public final class ThreadPoolBuilder {
    private static Logger logger = LoggerFactory.getLogger(ThreadPoolBuilder.class);
    private static CopyOnWriteArrayList<ThreadPoolExecutor> _threadPoolExecutors = new CopyOnWriteArrayList<>();
    private static final AtomicBoolean CLOSED = new AtomicBoolean(false);
    
   /**
    * The default rejected execution handler
    */
   public static PoolBuilder newBuilder() {
       return new PoolBuilder();
   }

   public static ScheduledPoolBuilder newScheduledBuilder() {
       return new ScheduledPoolBuilder();
   }

   public static void destroy() {
       if (!CLOSED.compareAndSet(false, true)) {
           return;
       }
       Iterator<ThreadPoolExecutor> iterator = _threadPoolExecutors.iterator();
       while (iterator.hasNext()) {
    	   ThreadPoolExecutor executor = iterator.next();
    	   destroy(executor);
        }
   }
   public static void destroy(ThreadPoolExecutor executor) {
       executor.shutdown();
       int retry = 3;
       while (retry > 0) {
          retry --;
          try {
              if (executor.awaitTermination(10, TimeUnit.SECONDS)) {
                  return;
              }
          } catch (InterruptedException e) {
              executor.shutdownNow();
              Thread.interrupted();
          } catch (Throwable ex) {
              if (executor instanceof NamingExecutor) {
                  logger.error("shutdown the executor:{} has error : {}", ((NamingExecutor)executor).getName(),ex);
              } else {
                  logger.error("shutdown the executor has error : {}", ex);
              }
          }
          executor.shutdownNow();
       }
       _threadPoolExecutors.remove(executor);
   }

   /**
    * Creates a new {@code NonInheritableExecutor} or {@code InheritableExecutor}
    * with the given initial parameters.
    *
    * @param poolName         the name of the thread pool
    * @param coreThreads      the number of threads to keep in the pool, even if they are
    *                         idle, unless {@code allowCoreThreadTimeOut} is set.
    * @param maximumThreads   the maximum number of threads to allow in the pool
    * @param keepAliveSeconds when the number of threads is greater than the core, this
    *                         is the maximum time (seconds) that excess idle threads will
    *                         wait for new tasks before terminating.
    * @param workQueue        the queue to use for holding tasks before they are executed.
    *                         This queue will hold only the {@code Runnable} tasks submitted
    *                         by the {@code execute} method.
    * @param threadFactory    the factory to use when the executor creates a new thread
    * @param rejectedHandler  the handler to use when execution is blocked because the
    *                         thread bounds and queue capacities are reached
    * @throws IllegalArgumentException if one of the following holds:<br>
    *         {@code corePoolSize < 0}<br>
    *         {@code keepAliveSeconds < 0}<br>
    *         {@code maximumPoolSize <= 0}<br>
    *         {@code maximumPoolSize < corePoolSize}
    * @throws NullPointerException if {@code workQueue}
    *         or {@code threadFactory} or {@code handler} is null
    */
   private static ThreadPoolExecutor newThreadPool(final String poolName,
                                                   final boolean inheritable,
                                                   final int coreThreads, final int maximumThreads,
                                                   final long keepAliveSeconds,
                                                   final BlockingQueue<Runnable> workQueue,
                                                   final ThreadFactory threadFactory,
                                                   final RejectedExecutionHandler rejectedHandler) {
       final TimeUnit unit = TimeUnit.SECONDS;
       if (inheritable) {
           return new InheritableExecutor(
                   coreThreads, 
                   maximumThreads, 
                   keepAliveSeconds, 
                   unit, 
                   workQueue,
                   threadFactory, 
                   rejectedHandler, 
                   poolName);
       }
       return new NonInheritableExecutor(
               coreThreads, 
               maximumThreads, 
               keepAliveSeconds, 
               unit, 
               workQueue,
               threadFactory, 
               rejectedHandler, 
               poolName);
       
   }

   /**
    * Creates a new ScheduledThreadPoolExecutor with the given
    * initial parameters.
    *
    * @param poolName        the name of the thread pool
    * @param coreThreads     the number of threads to keep in the pool, even if they are
    *                        idle, unless {@code allowCoreThreadTimeOut} is set.
    * @param threadFactory   the factory to use when the executor
    *                        creates a new thread
    * @param rejectedHandler the handler to use when execution is blocked because the
    *                        thread bounds and queue capacities are reached
    *
    * @throws IllegalArgumentException if {@code corePoolSize < 0}
    * @throws NullPointerException if {@code threadFactory} or
    *         {@code handler} is null
    * @return a new ScheduledThreadPoolExecutor
    */
   private static ScheduledThreadPoolExecutor newScheduledThreadPool(final String poolName,
                                                                     final boolean inheritable,
                                                                     final int coreThreads,
                                                                     final ThreadFactory threadFactory,
                                                                     final RejectedExecutionHandler rejectedHandler) {
       if (inheritable) {
           return new InheritableScheduledExecutor(
                   coreThreads,
                   threadFactory,
                   rejectedHandler,
                   poolName);
       }
       return new NonInheritableScheduledExecutor(
               coreThreads,
               threadFactory,
               rejectedHandler,
               poolName);
       
       
   }

   private ThreadPoolBuilder() {
   }

   public static class PoolBuilder {
       private String                   poolName;
       private Integer                  coreThreads;
       private Integer                  maximumThreads;
       private Long                     keepAliveSeconds;
       private BlockingQueue<Runnable>  workQueue;
       private boolean                  inheritable = false;//默认为不可继承
       private ThreadFactory            threadFactory = Executors.defaultThreadFactory();
       private RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

       public PoolBuilder poolName(final String poolName) {
           this.poolName = poolName;
           return this;
       }

       public PoolBuilder coreThreads(final Integer coreThreads) {
           this.coreThreads = coreThreads;
           return this;
       }

       public PoolBuilder maximumThreads(final Integer maximumThreads) {
           this.maximumThreads = maximumThreads;
           return this;
       }

       public PoolBuilder keepAliveSeconds(final Long keepAliveSeconds) {
           this.keepAliveSeconds = keepAliveSeconds;
           return this;
       }

       public PoolBuilder inheritable(final boolean inheritable) {
           this.inheritable = inheritable;
           return this;
       }
       
       public PoolBuilder workQueue(final BlockingQueue<Runnable> workQueue) {
           this.workQueue = workQueue;
           return this;
       }

       public PoolBuilder threadFactory(final ThreadFactory threadFactory) {
           this.threadFactory = threadFactory;
           return this;
       }

       public PoolBuilder rejectedHandler(final RejectedExecutionHandler handler) {
           this.handler = handler;
           return this;
       }

       public ThreadPoolExecutor build() {
           Requires.requireNonNull(this.poolName, "poolName");
           Requires.requireNonNull(this.inheritable, "inheritable");
           Requires.requireNonNull(this.coreThreads, "coreThreads");
           Requires.requireNonNull(this.maximumThreads, "maximumThreads");
           Requires.requireNonNull(this.keepAliveSeconds, "keepAliveSeconds");
           Requires.requireNonNull(this.workQueue, "workQueue");
           Requires.requireNonNull(this.threadFactory, "threadFactory");
           Requires.requireNonNull(this.handler, "handler");
           
           ThreadPoolExecutor threadPoolExecutor = 
                              ThreadPoolBuilder.newThreadPool(this.poolName, 
                                                              this.inheritable, 
                                                              this.coreThreads,
                                                              this.maximumThreads, 
                                                              this.keepAliveSeconds, 
                                                              this.workQueue, 
                                                              this.threadFactory, 
                                                              this.handler);
           _threadPoolExecutors.add(threadPoolExecutor);
           return threadPoolExecutor;
       }
   }

   public static class ScheduledPoolBuilder {
       private String                   poolName;
       private Integer                  coreThreads;
       private boolean                  inheritable = false;//默认为不可继承
       private ThreadFactory            threadFactory = Executors.defaultThreadFactory();
       private RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

       public ScheduledPoolBuilder poolName(final String poolName) {
           this.poolName = poolName;
           return this;
       }

       public ScheduledPoolBuilder coreThreads(final Integer coreThreads) {
           this.coreThreads = coreThreads;
           return this;
       }

       public ScheduledPoolBuilder threadFactory(final ThreadFactory threadFactory) {
           this.threadFactory = threadFactory;
           return this;
       }

       public ScheduledPoolBuilder rejectedHandler(final RejectedExecutionHandler handler) {
           this.handler = handler;
           return this;
       }
       
       public ScheduledPoolBuilder inheritable(final boolean inheritable) {
           this.inheritable = inheritable;
           return this;
       }

       public ScheduledThreadPoolExecutor build() {
           Requires.requireNonNull(this.poolName, "poolName");
           Requires.requireNonNull(this.inheritable, "inheritable");
           Requires.requireNonNull(this.coreThreads, "coreThreads");

           Requires.requireNonNull(this.threadFactory, "threadFactory");
           Requires.requireNonNull(this.handler, "handler");

           ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = 
                   ThreadPoolBuilder.newScheduledThreadPool(this.poolName, 
                                                            this.inheritable, 
                                                            this.coreThreads,
                                                            this.threadFactory, 
                                                            this.handler);
           _threadPoolExecutors.add(scheduledThreadPoolExecutor);
           return scheduledThreadPoolExecutor;
       }
   }
}

