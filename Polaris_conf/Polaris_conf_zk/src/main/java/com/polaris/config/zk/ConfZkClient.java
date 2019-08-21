package com.polaris.config.zk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

import com.polaris.core.config.ConfClient;
import com.polaris.core.config.ConfigHandlerProvider;
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
public class ConfZkClient implements Watcher {
	

	    
	private static final Logger logger = LoggerFactory.getLogger(ConfZkClient.class);

	// ------------------------------ zookeeper client ------------------------------
	private static ZooKeeper zooKeeper;
	private static ReentrantLock INSTANCE_INIT_LOCK = new ReentrantLock(true);
	public static ZooKeeper getInstance(){
		return getInstance(false);
	}

	public static ZooKeeper getInstance(boolean refresh){
		setZkAddress();
		if (zooKeeper==null || refresh) {
			try {
				if (INSTANCE_INIT_LOCK.tryLock(2, TimeUnit.SECONDS)) {
					try {
						
						//每次都刷新
						if (refresh) {
							if (zooKeeper != null) {
								zooKeeper.close();
								zooKeeper = null;
							}
						}
						zooKeeper = new ZooKeeper(ConfClient.getConfigRegistryAddress(), 20000, new Watcher() {
							@Override
							public void process(WatchedEvent watchedEvent) {
								try {
									logger.info(">>>>>>>>>> polaris_conf: watcher:{}", watchedEvent);

									// session expire, close old and create new
									if (watchedEvent.getState() == Event.KeeperState.Expired) {
										zooKeeper.close();
										zooKeeper = null;
										getInstance();
									}

									String path = watchedEvent.getPath();
									if (StringUtil.isEmpty(path)) {
										return;
									}
									String key = pathToKey(path);
									if (key != null) {
										// add One-time trigger
										zooKeeper.exists(path, true);
										if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
											ConfigHandlerProvider.removeValue(key, Constant.DEFAULT_CONFIG_NAME);
										} else if (watchedEvent.getType() == Event.EventType.NodeDataChanged || 
												watchedEvent.getType() == Event.EventType.NodeCreated) {
											String data = getPathData(path, false);
											ConfigHandlerProvider.updateValue(key, data, Constant.DEFAULT_CONFIG_NAME);
										} 
									}
								} catch (KeeperException e) {
									logger.error(e.getMessage());
								} catch (InterruptedException e) {
									logger.error(e.getMessage());
								}
							}
						});
						if (zooKeeper != null) {
							ConfZkClient.createWithParent(Constant.CONF_DATA_PATH);	// init cfg root path
						}
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
		if (zooKeeper == null) {
			throw new NullPointerException(">>>>>>>>>>> polaris_cache, ConfZkClient.zooKeeper is null.");
		}
		return zooKeeper;
	}
	
	/**
    * setZkAddress(Zookeeper地址设置)
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
    private static void setZkAddress() {
    	
    	//配置文件
    	if (StringUtil.isEmpty(ConfClient.getConfigRegistryAddress())) {
    		throw new NullPointerException(Constant.CONFIG_REGISTRY_ADDRESS_NAME + " is null");
    	}
    }
	    
	/**
	 * 监控所有被触发的事件(One-time trigger)
	 */
	@Override
	public void process(WatchedEvent event) {

	}

	// ------------------------------ util ------------------------------
	/**
	 * path 2 key
	 * @param nodePath
	 * @return ZnodeKey
	 */
	private static String pathToKey(String nodePath){
		if (nodePath.lastIndexOf(Constant.SLASH) > 0) {
			return nodePath.substring(nodePath.lastIndexOf(Constant.SLASH) + 1);
		}
		return null;
	}

	/**
	 * key 2 path
	 * @param nodeKey
	 * @return znodePath
	 */
	private static String keyToPath(String nodeKey){
		if (StringUtil.isEmpty(nodeKey)) {
			return Constant.CONF_DATA_PATH;
		}
		return Constant.CONF_DATA_PATH + Constant.SLASH + nodeKey;
	}

	/**
	 * create node path with parent path (如果父节点不存在,循环创建父节点, 因为父节点不存在zookeeper会抛异常)
	 * @param path	()
	 */
	private static Stat createWithParent(String path){
		// valid
		if (path==null || path.trim().length()==0) {
			return null;
		}

		try {
			Stat stat = getInstance().exists(path, false);
			if (stat == null) {
				//  valid parent, createWithParent if not exists
				if (path.lastIndexOf(Constant.SLASH) > 0) {
					String parentPath = path.substring(0, path.lastIndexOf(Constant.SLASH));
					Stat parentStat = getInstance().exists(parentPath, false);
					if (parentStat == null) {
						createWithParent(parentPath);
					}
				}
				// create desc node path
				try {
					zooKeeper.create(path, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				} catch (Exception ex) {
					logger.error(ex.getMessage());
				}
			}
			return getInstance().exists(path, false);
		} catch (KeeperException e) {
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * delete path by key
	 * @param key
	 */
	public static boolean deletePathByKey(String key){
		return deletePathByKey(key, true);
	}
	public static boolean deletePathByKey(String key, boolean isWatch){
		String path = keyToPath(key);
		try {
			Stat stat = getInstance().exists(path, isWatch);
			if (stat != null) {
				getInstance().delete(path, stat.getVersion());
				return true;
			} else {
				logger.info(">>>>>>>>>> zookeeper node path not found :{}", key);
			}
		} catch (KeeperException e) {
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	/**
	 * set data to node
	 * @param key
	 * @param data
	 * @return
	 */
	public static boolean setPathDataByKey(String key, String data) {
		return setPathDataByKey(key, data, true);
	}
	public static boolean setPathDataByKey(String key, String data, boolean isWatch) {
		String path = keyToPath(key);
		try {
			Stat stat = getInstance().exists(path, isWatch);
			if (stat == null) {
				createWithParent(path);
				stat = getInstance().exists(path, false);
			}
			if (data != null) {
				zooKeeper.setData(path, data.getBytes(Constant.UTF_CODE),stat.getVersion());
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	/**
	 * 根据应用名称获取ZK中所有的key
	 * @param appName
	 * @return
	 */
	public static List<String> getAllKeyByAppName(String appName, boolean isWatch){
        List<String> appsKey = new ArrayList<>();
		try {
			String path = keyToPath(appName);
			Stat stat = getInstance().exists(path, isWatch);
			if (stat != null) {
				appsKey = getInstance().getChildren(keyToPath(appName) ,isWatch);
				return appsKey;
			}
		} catch (KeeperException e) {
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		return  appsKey;
	}

	/**
	 * get children from path
	 * @param key
	 * @return
	 */
	public static List<String> getChildren(String path){
		return getChildren(path, true);
	}
	public static List<String>  getChildren(String path, boolean isWatch){
		String temppath = keyToPath(path);
		try {
			Stat stat = getInstance().exists(temppath, isWatch);
			if (stat != null) {
				return getInstance().getChildren(temppath, isWatch);
			} else {
				logger.info(">>>>>>>>>> znodeKey[{}] not found.", temppath);
			}
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
	public static String getPathDataByKey(String key){
		return getPathDataByKey(key, true);
	}
	public static String getPathDataByKey(String key, boolean isWatch){
		String path = keyToPath(key);
		return getPathData(path, isWatch);
	}
	private static String getPathData(String path, boolean isWatch){
		try {
			Stat stat = getInstance().exists(path, isWatch);//add watch
			if (stat != null) {
				String znodeValue = null;
				byte[] resultData = getInstance().getData(path, false, null);
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
	
	public static void close() {
		try {
			if (zooKeeper != null) {
				zooKeeper.close();
				zooKeeper = null;
			}
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
	}
}