package com.polaris.core.naming;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.NetUtils;

public abstract class NameingClient {

	public static void register() {
		//注册中心
		if (Constant.SWITCH_ON.equals(ConfClient.get(Constant.NAME_REGISTRY_SWITCH, Constant.SWITCH_ON))) {
			String registerIp = ConfClient.get(Constant.IP_ADDRESS, NetUtils.getLocalHost());
			ServerDiscoveryHandlerProvider.getInstance().register(registerIp, Integer.parseInt(ConfClient.get(Constant.SERVER_PORT_NAME)));
					
		}
	}
	
	public static void unRegister() {
		//注册中心
		if (Constant.SWITCH_ON.equals(ConfClient.get(Constant.NAME_REGISTRY_SWITCH, Constant.SWITCH_ON))) {
			String registerIp = ConfClient.get(Constant.IP_ADDRESS, NetUtils.getLocalHost());
        	ServerDiscoveryHandlerProvider.getInstance().deregister(registerIp, Integer.parseInt(ConfClient.get(Constant.SERVER_PORT_NAME)));
		}
	}
}
