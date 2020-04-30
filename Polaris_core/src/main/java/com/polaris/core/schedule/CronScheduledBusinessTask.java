package com.polaris.core.schedule;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.provider.ServerStrategyProviderFactory;
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
            List<String> list = ServerStrategyProviderFactory.get().getRealIpUrlList(ConfClient.getAppName());
            if (list == null || list.size() == 0) {
            	taskExecute();
            	return;
            }
            
            //get first ip for tast execute
            String scheduleIpAndPort = list.get(0);
            String localHost = NetUtils.getLocalHost();
            String localPort = ConfClient.get(Constant.SERVER_PORT_NAME);
            String registerIpAndPort = ConfClient.get(Constant.IP_ADDRESS, localHost) + ":" + localPort;
            
            //ip is ok
            if (registerIpAndPort.equals(scheduleIpAndPort)) {
            	
            	//task execute
            	taskExecute();
            }
        } catch (Exception e) {
            logger.error("ERROR", e);
        }
	}
	
	abstract public void taskExecute();

}
