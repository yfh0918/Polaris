package com.polaris.core.schedule;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.ServerDiscoveryHandlerProvider;
import com.polaris.core.util.NetUtils;

abstract public class CronScheduledBusinessTask implements Runnable {
    private Logger logger = LoggerFactory.getLogger(CronScheduledBusinessTask.class);

	@Override
	public void run() {
		try {
			
			//获取IP列表
            List<String> list = ServerDiscoveryHandlerProvider.getInstance().getAllUrl(ConfClient.getAppName(), false);
            if (list == null || list.size() == 0) {
            	taskExcute();
            	return;
            }
            
            //获取第一个IP
            String scheduleIpAndPort = list.get(0);
            String localHost = NetUtils.getLocalHost();
            String localPort = ConfClient.get(Constant.SERVER_PORT_NAME);
            String registerIpAndPort = ConfClient.get(Constant.IP_ADDRESS, localHost) + ":" + localPort;
            
            //符合要求的IP
            if (registerIpAndPort.equals(scheduleIpAndPort)) {
            	
            	//执行业务代码
            	taskExecute();
            }
        } catch (Exception e) {
            logger.error("ERROR", e);
        }
	}
	
	abstract public void taskExecute();

}
