package com.polaris.dubbo.filter;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.zookeeper.ChildListener;
import com.alibaba.dubbo.remoting.zookeeper.StateListener;
import com.alibaba.dubbo.remoting.zookeeper.support.AbstractZookeeperClient;
import com.alibaba.dubbo.remoting.zookeeper.zkclient.ZkClientWrapper;

public class ZkclientZookeeperClient extends AbstractZookeeperClient<IZkChildListener> {

    private final ZkClientWrapper client;

    private volatile KeeperState state = KeeperState.SyncConnected;
    
    private int timeout = 30000;

    public ZkclientZookeeperClient(URL url) {
        super(url);
        client = new ZkClientWrapper(url.getBackupAddress(), timeout);
        client.addListener(new IZkStateListener() {
            public void handleStateChanged(KeeperState state) throws Exception {
                ZkclientZookeeperClient.this.state = state;
                if (state == KeeperState.Disconnected) {
                    stateChanged(StateListener.DISCONNECTED);
                } else if (state == KeeperState.SyncConnected) {
                    stateChanged(StateListener.CONNECTED);
                }
            }

            public void handleNewSession() throws Exception {
                stateChanged(StateListener.RECONNECTED);
            }

			@Override
			public void handleSessionEstablishmentError(Throwable error) throws Exception {
				// TODO Auto-generated method stub
				
			}
        });
        client.start();
        
        //定时刷新ZK
        refreshZK(this, timeout);
    }

    //启动线程监控定时刷新zk节点（连接问题导致的session过期，消费者无法继续调用的问题）
    private static void refreshZK(ZkclientZookeeperClient zkClient, long warchTime){
    	/*
    	Thread run = new Thread(new Runnable(){
    		 @Override  
             public void run() {
    			 while(true){  
                 	try {
 						Thread.sleep(warchTime);
 						if (ConfClient.get("dubbo.registry.refresh",Constant.SWITCH_OFF).equals(Constant.SWITCH_ON)) {
 	 						zkClient.stateChanged(StateListener.RECONNECTED);
 						}
 					} catch (Exception e) {
 						logger.error(e);
 					}
                  }
    		 }  
    	});
    	run.setDaemon(true);//守护线程
    	run.start();
    	*/
    }

    public void createPersistent(String path) {
        try {
            client.createPersistent(path);
        } catch (ZkNodeExistsException e) {
        }
    }

    public void createEphemeral(String path) {
        try {
            client.createEphemeral(path);
        } catch (ZkNodeExistsException e) {
        }
    }

    public void delete(String path) {
        try {
            client.delete(path);
        } catch (ZkNoNodeException e) {
        }
    }

    public List<String> getChildren(String path) {
        try {
            return client.getChildren(path);
        } catch (ZkNoNodeException e) {
            return null;
        }
    }

    public boolean checkExists(String path) {
        try {
            return client.exists(path);
        } catch (Throwable t) {
        }
        return false;
    }

    public boolean isConnected() {
        return state == KeeperState.SyncConnected;
    }

    public void doClose() {
        client.close();
    }

    public IZkChildListener createTargetChildListener(String path, final ChildListener listener) {
        return new IZkChildListener() {
            public void handleChildChange(String parentPath, List<String> currentChilds)
                    throws Exception {
                listener.childChanged(parentPath, currentChilds);
            }
        };
    }

    public List<String> addTargetChildListener(String path, final IZkChildListener listener) {
        return client.subscribeChildChanges(path, listener);
    }

    public void removeTargetChildListener(String path, IZkChildListener listener) {
        client.unsubscribeChildChanges(path, listener);
    }

}
