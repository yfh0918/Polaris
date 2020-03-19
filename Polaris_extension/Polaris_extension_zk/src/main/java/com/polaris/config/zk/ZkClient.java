package com.polaris.config.zk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.util.StringUtil;


/**
 * ZooKeeper cfg client (Watcher + some utils)
 *
 *         Zookeeper
 *         从设计模式角度来看，是一个基于观察者模式设计的分布式服务管理框架，它负责存储和管理大家都关心的数据，然后接受观察者的注册
 *         ，一旦这些数据的状态发生变化，Zookeeper 就将负责通知已经在 Zookeeper
 *         上注册的那些观察者做出相应的反应，从而实现集群中类似 Master/Slave 管理模式
 *
 *         1、统一命名服务（Name Service）:将有层次的目录结构关联到一定资源上，广泛意义上的关联，也许你并不需要将名称关联到特定资源上，
 *         你可能只需要一个不会重复名称。
 *
 *         2、配置管理（Configuration Management）：分布式统一配置管理：将配置信息保存在
 *         Zookeeper 的某个目录节点中，然后将所有需要修改的应用机器监控配置信息的状态，一旦配置信息发生变化，每台应用机器就会收到
 *         Zookeeper 的通知，然后从 Zookeeper 获取新的配置信息应用到系统中
 *
 *         3、集群管理（Group
 *         Membership）:Zookeeper 能够很容易的实现集群管理的功能，如有多台 Server 组成一个服务集群，那么必须
 *         要一个“总管”知道当前集群中每台机器的服务状态，一旦有机器不能提供服务，集群中其它集群必须知道，从而做出调整重新分配服务策略。
 *         同样当增加集群的服务能力时，就会增加一台或多台 Server，同样也必须让“总管”知道。
 *
 *         4、共享锁（Locks）：
 *         5、队列管理：a、当一个队列的成员都聚齐时，这个队列才可用，否则一直等待所有成员到达，这种是同步队列。b、队列按照 FIFO 方式
 *         进行入队和出队操作，例如实现生产者和消费者模型。
 *
 *         集中式配置管理 动态更新
 *
 */
public class ZkClient implements Watcher {
	
	private static final Logger logger = LoggerFactory.getLogger(ZkClient.class);

	// ------------------------------ zookeeper client ------------------------------
	private static ReentrantLock INSTANCE_INIT_LOCK = new ReentrantLock(true);
	private static Map<String, ZkInfo> zkMap = new ConcurrentHashMap<>();
	public static ZooKeeper getInstance(String url, int sessionTimeoutMs){
		return getInstance(url, sessionTimeoutMs, false);
	}

	public static ZooKeeper getInstance(String url, int sessionTimeoutMs, boolean refresh){
    	if (StringUtil.isEmpty(url)) {
    		throw new NullPointerException("url is null");
    	}
    	ZkInfo zkInfo = zkMap.get(url);
		if (zkInfo == null || refresh) {
			try {
				if (INSTANCE_INIT_LOCK.tryLock(2, TimeUnit.SECONDS)) {
					try {
						
						//每次都刷新
						if (refresh) {
							if (zkInfo != null) {
								zkInfo.clear();
								zkMap.remove(url);
							}
						}
						zkMap.put(url, new ZkInfo(new ZooKeeper(url, sessionTimeoutMs, new Watcher() {
							@Override
							public void process(WatchedEvent watchedEvent) {
								try {
									logger.info(">>>>>>>>>> polaris_conf: watcher:{}", watchedEvent);
									ZkInfo zkinfo = zkMap.get(url);
									// session expire, close old and create new
									if (watchedEvent.getState() == Event.KeeperState.Expired) {
										zkinfo.clear();
										zkMap.remove(url);
										getInstance(url,sessionTimeoutMs);
									}

									String path = watchedEvent.getPath();
									if (StringUtil.isEmpty(path)) {
										return;
									}
									// add One-time trigger
									zkinfo.getZk().exists(path, true);
									
									//获取监听者
									for (ZkListener zkListener : zkinfo.getListeners(path)) {
										zkListener.listen(url, path, watchedEvent.getType());
									}
									
									
								} catch (KeeperException e) {
									logger.error(e.getMessage());
								} catch (InterruptedException e) {
									logger.error(e.getMessage());
								}
							}
						})));
						zkInfo = zkMap.get(url);
						
					} finally {
						INSTANCE_INIT_LOCK.unlock();
					}
                }
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		if (zkInfo == null) {
			throw new NullPointerException(">>>>>>>>>>> url:"+url+" zooKeeper is null.");
		}
		return zkInfo.getZk();
	}
	
	public static void addZkListener(String url, String path, ZkListener listener) {
		zkMap.get(url).getListeners(path).add(listener);
	}

	    
	/**
	 * 监控所有被触发的事件(One-time trigger)
	 */
	@Override
	public void process(WatchedEvent event) {
	}

	/**
	 * create node path with parent path (如果父节点不存在,循环创建父节点, 因为父节点不存在zookeeper会抛异常)
	 * @param path	()
	 */
	public static Stat createWithParent(ZooKeeper zk, String path){
		// valid
		if (path==null || path.trim().length()==0) {
			return null;
		}

		try {
			Stat stat = zk.exists(path, false);
			if (stat == null) {
				//  valid parent, createWithParent if not exists
				if (path.lastIndexOf(Constant.SLASH) > 0) {
					String parentPath = path.substring(0, path.lastIndexOf(Constant.SLASH));
					Stat parentStat = zk.exists(parentPath, false);
					if (parentStat == null) {
						createWithParent(zk, parentPath);
					}
				}
				// create desc node path
				try {
					zk.create(path, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				} catch (Exception ex) {
					logger.error(ex.getMessage());
				}
			}
			return zk.exists(path, false);
		} catch (KeeperException e) {
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	/**
	 * get data from node
	 * @param key
	 * @return
	 */
	public static String getPathData(ZooKeeper zk, String path){
		try {
			Stat stat = zk.exists(path, false);//add watch
			if (stat != null) {
				String znodeValue = null;
				byte[] resultData = zk.getData(path, false, null);
				if (resultData != null) {
					znodeValue = new String(resultData,Constant.UTF_CODE);
				}
				return znodeValue;
			} else {
				logger.info(">>>>>>>>>> znodeKey[{}] not found.", path);
			}
		} catch (KeeperException e) {
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	/**
	 * add watch
	 * @param key
	 * @return
	 */
	public static void addWatchForPath(ZooKeeper zk, String path) {
		try {
			Stat stat = zk.exists(path, true);
			if (stat == null) {
				logger.info(">>>>>>>>>> znodeKey[{}] not found.", path);
			}
		} catch (KeeperException e) {
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public static class ZkInfo {
		private ZooKeeper zk;
		private Map<String, List<ZkListener>> zkListerners = new ConcurrentHashMap<>();
		ZkInfo(ZooKeeper zk) {
			this.zk = zk;
		}
		public List<ZkListener> getListeners(String path) {
			List<ZkListener> list = zkListerners.get(path);
			if (list == null) {
				synchronized(path.intern()) {
					list = zkListerners.get(path);
					if (list == null) {
						list = new ArrayList<>();
						zkListerners.put(path, list);
					}
				}
			}
			return list;
		}
		public ZooKeeper getZk() {
			return zk;
		}
		public void clear() {
			try {
				zk.close();
			} catch (InterruptedException e) {
				logger.error("Error:",e);
			}
			zk = null;
			for (List<ZkListener> list : zkListerners.values()) {
				list.clear();
			}
			zkListerners.clear();
			zkListerners = null;
		}
	}

}