package com.polaris.naming;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.ServerHandler;
import com.polaris.core.naming.ServerHandlerOrder;
import com.polaris.core.util.StringUtil;
import com.polaris.core.util.WeightedRoundRobinScheduling;
import com.polaris.core.util.WeightedRoundRobinScheduling.Server;

@Order(ServerHandlerOrder.ZK)
public class ZkServer implements ServerHandler {
	private static final Logger logger = LoggerFactory.getLogger(ZkServer.class);
	private CuratorFramework curator;
	private Map<String, PathChildrenCache> pathCache = new ConcurrentHashMap<>();
	private Map<String, WeightedRoundRobinScheduling> pathWeight = new ConcurrentHashMap<>();
	public ZkServer() {
	}
	
	private CuratorFramework getCurator() {
		if (curator == null) {
			synchronized(this) {
				if (curator == null) {
					curator = CuratorFrameworkFactory.newClient(ConfClient.getNamingRegistryAddress(), 
							5000, 3000, new RetryNTimes(5, 1000));
					curator.start();
				}
			}
		}
		return curator;
	}
	@Override
	public String getUrl(String key) {
		
		//get curator
		CuratorFramework curator = getCurator();
		String childNodePathCache = getPath(key);

        //childData：设置缓存节点的数据状态
		PathChildrenCache childrenCache = pathCache.get(childNodePathCache);
		try {
					
			if (childrenCache == null) {
				synchronized(childNodePathCache.intern()) {
					if (pathCache.get(childNodePathCache) == null) {
						childrenCache = new PathChildrenCache(curator,childNodePathCache,true);

				        /*
				        * StartMode：初始化方式
				        * POST_INITIALIZED_EVENT：异步初始化。初始化后会触发事件
				        * NORMAL：异步初始化
				        * BUILD_INITIAL_CACHE：同步初始化
				        * */
				        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

				        //获取所有子节点
						List<WeightedRoundRobinScheduling.Server> serverList = new ArrayList<>();
						List<ChildData> childDataList = childrenCache.getCurrentData();
				        for(ChildData cd : childDataList){
				        	String serverInfo = new String(cd.getData());
							String[] si = serverInfo.split(":");
				            if (si.length == 2) {
					                WeightedRoundRobinScheduling.Server server = new WeightedRoundRobinScheduling.Server(si[0], Integer.valueOf(si[1]), 1);
				                serverList.add(server);
				            } else if (si.length == 3) {
					                WeightedRoundRobinScheduling.Server server = new WeightedRoundRobinScheduling.Server(si[0], Integer.valueOf(si[1]), Integer.valueOf(si[2]));
				                serverList.add(server);
				            }
				        }
						WeightedRoundRobinScheduling wrrs = new WeightedRoundRobinScheduling(serverList);
						pathWeight.put(childNodePathCache, wrrs);

				        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
				            public void childEvent(CuratorFramework ient, PathChildrenCacheEvent event) throws Exception {
				               if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
				            	   String serverInfo = new String(event.getData().getData());
				            	   String[] si = serverInfo.split(":");
				            	   wrrs.add(new WeightedRoundRobinScheduling.Server(si[0], Integer.valueOf(si[1]), Integer.valueOf(si[2])));
				               }else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)){
				            	   String serverInfo = new String(event.getData().getData());
				            	   String[] si = serverInfo.split(":");
				            	   wrrs.remove(wrrs.getServer(si[0], Integer.valueOf(si[1])));
				               }
				            }
				        });
				        pathCache.put(childNodePathCache, childrenCache);
					}
				}
			}
	        
		} catch (Exception ex) {
			logger.error("ERROR:",ex);
		}
    	Server server = pathWeight.get(childNodePathCache).getServer();
		return server.toString();
	}
	
	@Override
	public List<String> getAllUrls(String key) {
		return getAllUrls(key, true);
	}

	@Override
	public List<String> getAllUrls(String key, boolean subscribe) {
		//get curator
		String childNodePathCache = getPath(key);
		PathChildrenCache childrenCache = pathCache.get(childNodePathCache);
		//获取所有子节点
        List<ChildData> childDataList = childrenCache.getCurrentData();
        List<String> childList = new ArrayList<>();
        for(ChildData cd : childDataList){
        	String data = new String(cd.getData());
        	String[] datas = data.split(":");
    		childList.add(datas[0]+":"+datas[1]);
        }
        return childList;
	}

	@Override
	public void connectionFail(String key, String url) {
	}

	@Override
	public void register(String ip, int port) {
		
		//get curator
		CuratorFramework curator = getCurator();
		
		//register-data
		String regContent = ip + ":" + port+ ":" + ConfClient.get(Constant.PROJECT_WEIGHT, Constant.PROJECT_WEIGHT_DEFAULT);
		String zkRegPathPrefix = getPath(ConfClient.getAppName()) + "service-provider-";
		
		//re-connect
		ZkConnectionStateListener stateListener = new ZkConnectionStateListener(zkRegPathPrefix, regContent);
		curator.getConnectionStateListenable().addListener(stateListener);
		
		//create node
		try {
			curator.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
			.forPath(zkRegPathPrefix, regContent.getBytes(Constant.UTF_CODE));
		} catch (Exception ex) {
			logger.error("ERROR:",ex);
		}
	}

	@Override
	public void deregister(String ip, int port) {
		for (PathChildrenCache cache : pathCache.values()) {
			try {
				cache.close();
			} catch (IOException ex) {
				logger.error("ERROR:",ex);
			}
		}
		getCurator().close();
	}
	
	private static String getPath(String key) {
		StringBuilder groupSb = new StringBuilder();
		
		//rootPath
		groupSb.append(ConfClient.get("naming.zk.root.path",Constant.NAMING_ROOT_PATH));
		groupSb.append(Constant.SLASH);

		//namespace
		String nameSpace = ConfClient.getNameSpace();
		if (StringUtil.isNotEmpty(nameSpace)) {
			groupSb.append(nameSpace);
			groupSb.append(Constant.SLASH);
		}
		
		//cluster
		if (StringUtil.isNotEmpty(ConfClient.getGroup())) {
			groupSb.append(ConfClient.getGroup());
			groupSb.append(Constant.SLASH);
		}
		
		//key
		groupSb.append(key);
		groupSb.append(Constant.SLASH);
		
		//返回
		return groupSb.toString();
	}

}
