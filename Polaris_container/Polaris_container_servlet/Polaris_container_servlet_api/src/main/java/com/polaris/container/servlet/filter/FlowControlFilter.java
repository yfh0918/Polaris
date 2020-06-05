package com.polaris.container.servlet.filter;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.pojo.Result;
import com.polaris.core.util.JacksonUtil;

/**
* 流量控制过滤器
* 用于保护当前JVM进程，在过载流量下，处于稳定可控状态。
*/
public class FlowControlFilter implements Filter {

	//最大并发量
	private int permits = 100;//默认为200
	
	//当并发量达到permits后，新的请求将会被buffer，buffer最大尺寸
	//如果buffer已满，则直接拒绝
	private int bufferSize = 150;
	
	//buffer中的请求被阻塞，此值用于控制最大阻塞时间
	private long timeout = 30000;//默认阻塞时间
	private BlockingQueue<Node> waitingQueue;
	
	//守护线程
	private Thread selectorThread;
	
	//信号量
	private Semaphore semaphore;
	
	//锁
	private Object lock = new Object();
	
	//流量控制信息
	private static final String FLOW_CONTROL_MESSAGE = "请求已满，请稍后再试";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		//application.properties中定义的最大线程数
		String p = ConfClient.get("server.flowcontrol.permits");
		if(p != null) {
			permits = Integer.parseInt(p);
			if(permits < 0) {
				throw new IllegalArgumentException("FlowControlFilter,permits parameter should be greater than 0 !");
			}
		}

		//application.properties中定义的最大等待时间
		String t = ConfClient.get("server.flowcontrol.timeout");
		if(t != null) {
			timeout = Long.parseLong(t);
			if(timeout < 1) {
				throw new IllegalArgumentException("FlowControlFilter,timeout parameter should be greater than 0 !");
			}
		}

		//application.properties中定义的等待队列的最大尺度
		String b = ConfClient.get("server.flowcontrol.bufferSize");
		if(b != null) {
			bufferSize = Integer.parseInt(b);
			if(bufferSize < 0) {
				throw new IllegalArgumentException("FlowControlFilter,bufferSize parameter should be greater than 0 !");
			}
		}
		waitingQueue = new LinkedBlockingQueue<>(bufferSize);
		
		//定义信号量
		semaphore = new Semaphore(permits);
		selectorThread = new Thread(new SelectorRunner());
		selectorThread.setDaemon(true);
		selectorThread.start();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		//检查守护线程
		checkSelector();
		Thread t = Thread.currentThread();
		Node node = new Node(t,false);
		boolean buffered = waitingQueue.offer(node);

		//如果buffer已满
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;
		if (!buffered) {
			//直接返回
			setRtnResponse(httpServletResponse);
			return;
		}
		
		//进入等待队列后，当前线程阻塞
		LockSupport.parkNanos(this, TimeUnit.MICROSECONDS.toNanos(System.currentTimeMillis() + timeout));
		
		//消除中断标识
		if (t.isInterrupted()) {
			Thread.interrupted();//clear status
		}
		
		//开始操作
		synchronized (lock) {

			//线程超时的unpark
			if(!node.dequeued) {
				node.dequeued = true;//标识成出队，直接抛弃处理
				
				//直接返回
				setRtnResponse(httpServletResponse);
				return;	
			}
		}

		//继续执行
		try {
			chain.doFilter(request,response);
		} finally {
			
			//semaphore.acquire()时的中断异常，就不需要semaphore.release()
			if (node.acquired) {
				semaphore.release();//调用结束释放信号量
			}

			//检查守护进程
			checkSelector();
		}
	}
	
	//设置返回值
	@SuppressWarnings("rawtypes")
	private void setRtnResponse(HttpServletResponse httpServletResponse) throws IOException  {
		httpServletResponse.setStatus(200);
		Result responseDto = new Result();
		responseDto.setMessage(FLOW_CONTROL_MESSAGE);
		responseDto.setCode(Constant.RESULT_FAIL);
		httpServletResponse.getWriter().write(JacksonUtil.toJson(responseDto));
		httpServletResponse.getWriter().flush();
	}
	
	//检查守护线程
	private void checkSelector() {
		
		//如果守护线程挂了，重启一个守护线程
		if(selectorThread == null || !selectorThread.isAlive()) {
			synchronized (lock) {
				if(selectorThread == null || !selectorThread.isAlive()) {
					selectorThread = new Thread(new SelectorRunner());
					selectorThread.setDaemon(true);
					selectorThread.start();
				}
			}
		}
	}
	
	//守护线程
	private class SelectorRunner implements Runnable {

		@Override
		public void run() {
			while (true) {

				//获取阻塞线程
				Node node = null;
				try {
					node = waitingQueue.take();
					synchronized (lock) {
						
						//如果此线程已经park过期而退出了，则直接忽略
						if(node.dequeued) {
							continue;
						} else {
							node.dequeued = true;
						}
					}
					//占用一个信号位
					semaphore.acquire();
				} catch (InterruptedException e) {
					
					//semaphore.acquire()的中断异常
					if (node != null) {
						node.acquired = false;
					}
					
					//存在中断异常，重新设置中断标识
					Thread.currentThread().interrupt();
				} finally {
					if (node != null) {
						LockSupport.unpark(node.currentThread);
					}
				}
			}
		}
	}
	
	//请求节点
	private class Node {
		Thread currentThread;
		boolean dequeued;//是否已经出队
		boolean acquired;//是否acquired
		public Node(Thread t,boolean dequeued) {
			this.currentThread = t;
			this.dequeued = dequeued;
			this.acquired = true;
		}
	}
	
	@Override
	public void destroy() {
		selectorThread.interrupt();
	}
}
