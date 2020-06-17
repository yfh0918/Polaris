package com.polaris.core.naming;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.provider.NamingUrlHandlerFactory;
import com.polaris.core.pojo.Server;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.StringUtil;

public abstract class NamingClient {
	private static final Logger logger = LoggerFactory.getLogger(NamingClient.class);
	public static boolean register() {
		if (Constant.SWITCH_ON.equals(ConfClient.get(Constant.NAME_REGISTRY_SWITCH, Constant.SWITCH_ON))) {
			String port = ConfClient.get(Constant.SERVER_PORT_NAME);
			if (StringUtil.isEmpty(port)) {
				return false;
			}
			String registerIp = ConfClient.get(Constant.IP_ADDRESS, NetUtils.getLocalHost());
			boolean result = NamingUrlHandlerFactory.get().register(Server.of(registerIp, Integer.parseInt(port)));
			logger.debug("register ip:{}, port:{} , result:{}",registerIp,port,result);
			return result;
		}
		return false;
	}
	
	public static boolean unRegister() {
		if (Constant.SWITCH_ON.equals(ConfClient.get(Constant.NAME_REGISTRY_SWITCH, Constant.SWITCH_ON))) {
			String port = ConfClient.get(Constant.SERVER_PORT_NAME);
			if (StringUtil.isEmpty(port)) {
				return false;
			}
			String registerIp = ConfClient.get(Constant.IP_ADDRESS, NetUtils.getLocalHost());
			boolean result =  NamingUrlHandlerFactory.get().deregister(Server.of(registerIp, Integer.parseInt(port)));
			logger.debug("unRegister ip:{}, port:{} , result:{}",registerIp,port,result);
			return result;
		}
		return false;
	}
	

	public static Server getServer(String serviceName) {
	    return NamingUrlHandlerFactory.get().getServer(serviceName);
	}

	public static List<Server> getServerList(String serviceName) {
	    return NamingUrlHandlerFactory.get().getServerList(serviceName);
	}

	public static void onConnectionFail(Server server) {
	    NamingUrlHandlerFactory.get().onConnectionFail(server);
	}
	
	public static String getRealIpUrl(String serviceNameUrl) {
	    return NamingUrlHandlerFactory.get().getRealIpUrl(serviceNameUrl);
	}

	public static List<String> getRealIpUrlList(String serviceNameUrl) {
	    return NamingUrlHandlerFactory.get().getRealIpUrlList(serviceNameUrl);
	}
}
