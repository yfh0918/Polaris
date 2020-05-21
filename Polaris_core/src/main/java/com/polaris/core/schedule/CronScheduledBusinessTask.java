package com.polaris.core.schedule;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.provider.ServerStrategyProviderFactory;
import com.polaris.core.pojo.Server;
import com.polaris.core.util.NetUtils;

/**
 * 配合注册中心实现集群定时器
 *
 * @author Tom yu
 */
abstract public class CronScheduledBusinessTask implements Runnable {
    private Logger logger = LoggerFactory.getLogger(CronScheduledBusinessTask.class);

	@Override
	public void run() {
		try {
			
			//get cluster ip list
            List<Server> list = ServerStrategyProviderFactory.get().getServerList(ConfClient.getAppName());
            if (list == null || list.size() == 0) {
            	taskExecute();
            	return;
            }
            
            //get first Server for tast execute
            Server server = list.get(0);
            String localIp = ConfClient.get(Constant.IP_ADDRESS,NetUtils.getLocalHost());
            Integer localPort = Integer.parseInt(ConfClient.get(Constant.SERVER_PORT_NAME));
            Server localServer = Server.of(localIp, localPort);
            if (server.equals(localServer)) {
            	taskExecute();
            }
        } catch (Exception e) {
            logger.error("ERROR", e);
        }
	}
	
	abstract public void taskExecute();

}
