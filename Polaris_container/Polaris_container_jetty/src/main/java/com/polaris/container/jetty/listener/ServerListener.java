package com.polaris.container.jetty.listener;

/**
 * Class Name : ServerStatusListener
 * Description : 服务器监听器
 * Creator : yufenghua
 * Modifier : yufenghua
 *
 */

public interface ServerListener {
	
	/**
	 * 服务器启动状态
	 */
	public static final int SERVER_STATUS_STARTED = 1;

	/**
	 * 服务器停止状态
	 */
	public static final int SERVER_STATUS_STOPPED = 0;

	/**
	 * 服务器异常
	 */
	public static final int SERVER_STATUS_ERROR = 2;

	/** 服务器运行状态变更响应
	 * @param serverStatus 服务器运行状态
	 */
	void onServerStatusChanged(int serverStatus);
}
