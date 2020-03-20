package com.polaris.config.zk;

import org.apache.zookeeper.Watcher.Event.EventType;

public interface ZkListener {
	public void listen(String path, EventType type);
}
