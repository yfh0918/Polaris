package com.polaris.naming;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* A class to monitor connection state & re-register to Zookeeper when connection lost.
*
* @author Darran Zhang @ codelast.com
*/
public class ZkConnectionStateListener implements ConnectionStateListener {
	private static final Logger logger = LoggerFactory.getLogger(ZkConnectionStateListener.class);

	private String zkRegPathPrefix;
	private String regContent;
	public ZkConnectionStateListener(String zkRegPathPrefix, String regContent) {
		this.zkRegPathPrefix = zkRegPathPrefix;
		this.regContent = regContent;
	}
	@Override
	public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
		if (connectionState == ConnectionState.LOST) {
			while (true) {
				try {
					if (curatorFramework.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
						curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
						.forPath(zkRegPathPrefix, regContent.getBytes(Constant.UTF_CODE));
						break;
					}
				} catch (InterruptedException e) {
					logger.error("ERROR:",e);
					break;
				} catch (Exception e) {
					logger.error("ERROR:",e);
				}
			}
		}
	}
}
