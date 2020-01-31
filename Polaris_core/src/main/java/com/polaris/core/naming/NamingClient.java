package com.polaris.core.naming;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.StringUtil;

public abstract class NamingClient {

	public static boolean register() {
		//注册中心
		if (Constant.SWITCH_ON.equals(ConfClient.get(Constant.NAME_REGISTRY_SWITCH, Constant.SWITCH_ON))) {
			String port = ConfClient.get(Constant.SERVER_PORT_NAME);
			if (StringUtil.isEmpty(port)) {
				return false;
			}
			String registerIp = ConfClient.get(Constant.IP_ADDRESS, NetUtils.getLocalHost());
			return ServerDiscoveryHandlerProvider.getInstance().register(registerIp, Integer.parseInt(port));
		}
		return false;
	}
	
	public static boolean unRegister() {
		//注册中心
		if (Constant.SWITCH_ON.equals(ConfClient.get(Constant.NAME_REGISTRY_SWITCH, Constant.SWITCH_ON))) {
			String port = ConfClient.get(Constant.SERVER_PORT_NAME);
			if (StringUtil.isEmpty(port)) {
				return false;
			}
			String registerIp = ConfClient.get(Constant.IP_ADDRESS, NetUtils.getLocalHost());
        	return ServerDiscoveryHandlerProvider.getInstance().deregister(registerIp, Integer.parseInt(port));
		}
		return false;
	}
}
