package com.polaris.naming;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.polaris.core.naming.ServerHandler;
import com.polaris.core.naming.ServerHandlerOrder;

@Order(ServerHandlerOrder.ZK)
public class ZkServer implements ServerHandler {
	private static final Logger logger = LoggerFactory.getLogger(ZkServer.class);
	public ZkServer() {
	}
	
	@Override
	public String getUrl(String key) {

		return null;
	}
	
	@Override
	public List<String> getAllUrls(String key) {
		return getAllUrls(key, true);
	}

	@Override
	public List<String> getAllUrls(String key, boolean subscribe) {

		return null;
	}

	@Override
	public void connectionFail(String key, String url) {
		//nothing
	}

	@Override
	public void register(String ip, int port) {
	}

	@Override
	public void deregister(String ip, int port) {
	}

}
