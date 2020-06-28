package com.polaris.core.thread.cron;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.NamingClient;
import com.polaris.core.pojo.Server;
import com.polaris.core.util.NetUtils;

/**
 * 配合注册中心实现集群定时器
 *
 * @author Tom yu
 */
abstract public class CronScheduledBusinessTask implements Runnable {
    private Logger logger = LoggerFactory.getLogger(CronScheduledBusinessTask.class);
    private final String appName;
    private final String ip;
    private final Integer port;

    protected CronScheduledBusinessTask() {
        this(ConfClient.getAppName(),
             ConfClient.get(Constant.IP_ADDRESS,NetUtils.getLocalHost()),
             Integer.parseInt(ConfClient.get(Constant.SERVER_PORT_NAME)));
    }
    protected CronScheduledBusinessTask(String appName, String localIp, Integer localPort) {
        this.appName = appName;
        this.ip = localIp;
        this.port = localPort;
    }
    
	@Override
	public void run() {
		try {
			
			//get cluster ip list
            List<Server> list = NamingClient.getServerList(appName);
            if (list == null || list.size() == 0) {
            	taskExecute();
            	return;
            }
            
            //get first Server for tast execute
            Server server = list.get(0);
            Server localServer = Server.of(ip, port);
            if (server.equals(localServer)) {
            	taskExecute();
            }
        } catch (Exception e) {
            logger.error("ERROR", e);
        }
	}
	
	abstract public void taskExecute();

}
