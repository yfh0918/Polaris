package com.polaris.gateway;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.littleshoot.proxy.HostResolver;

import com.github.pagehelper.util.StringUtil;
import com.polaris.comm.Constant;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.config.ConfListener;
import com.polaris.core.connect.ServerDiscoveryHandlerProvider;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class HostResolverImpl implements HostResolver {
    private volatile static HostResolverImpl singleton;
    private volatile Map<String, String> serverMap = new ConcurrentHashMap<>();
    private volatile Map<String, String> uriMap = new ConcurrentHashMap<>();
    public static final String UPSTREAM = "upstream.txt";

    //载入需要代理的IP(需要动态代理)
    private void loadUpstream(String content) {
    	if (StringUtil.isEmpty(content)) {
    		return;
    	}

    	Map<String, String> tempServerMap = new ConcurrentHashMap<>();
    	Map<String, String> tempUriMap = new ConcurrentHashMap<>();
    	Map<String, String> servers = new HashMap<>();
    	String[] contents = content.split(Constant.LINE_SEP);
		for (String detail : contents) {
			int index = detail.indexOf("=");
			if (index >= 0) {
				String key = detail.substring(0, index).trim();
				String value = "";
				if (index < detail.length()) {
					value = detail.substring(index + 1).trim();
				}
				servers.put(key, value);
			}
			
		}
        for (Map.Entry<String, String> entry : servers.entrySet()) {
            String hostInfo = entry.getKey();
            
            if (hostInfo.startsWith(GatewayConstant.PORT) || GatewayConstant.DEFAULT.equals(hostInfo)) {
            	
            	//不是默认的端口号(key=7001, value=192.168.5.101:7720,192.168.5.101:7721)
            	if (hostInfo.startsWith(GatewayConstant.PORT)) {
            		tempServerMap.put(hostInfo.substring(GatewayConstant.PORT.length()), entry.getValue());
            	} else {
            		
            		//默认的端口号(key=9090, value=192.168.5.101:9090,192.168.5.101:9001)
            		tempServerMap.put(GatewayConstant.SERVER_PORT, entry.getValue());
            	}
            	
            } else {
            	tempUriMap.put(hostInfo, entry.getValue());
            }
        }
    	serverMap = tempServerMap;
    	uriMap = tempUriMap;
    }
    
    //构造函数（单例）
    private HostResolverImpl() {
    	loadUpstream(ConfClient.getFileContent(UPSTREAM));//载入配置文件
    	ConfClient.addListener(UPSTREAM, new ConfListener() {
			@Override
			public void receive(String content) {
				loadUpstream(content);
			}
    		
    	});
    }
    public static HostResolverImpl getSingleton() {
        if (singleton == null) {
            synchronized (HostResolverImpl.class) {
                if (singleton == null) {
                    singleton = new HostResolverImpl();
                }
            }
        }
        return singleton;
    }

    //获取服务
    String getServers(String key) {
    	return serverMap.get(key);
    }
    String getPort(String uri) {
    	if (uri != null) {
        	for (Entry<String, String> entry : uriMap.entrySet()) {
        		if (uri.startsWith(entry.getKey())) {
        			return entry.getValue();
        		}
        		
        	}    	
        }
    	return GatewayConstant.SERVER_PORT;
    }

    @Override
    public InetSocketAddress resolve(String host, int port)
            throws UnknownHostException {
    	
    	String key = String.valueOf(port);
        if (serverMap.containsKey(key)) {
        	String uri = ServerDiscoveryHandlerProvider.getInstance().getUrl(serverMap.get(key));
            if (StringUtil.isNotEmpty(uri)) {
            	String[] si = uri.split(":");
                return new InetSocketAddress(si[0], Integer.parseInt(si[1]));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
