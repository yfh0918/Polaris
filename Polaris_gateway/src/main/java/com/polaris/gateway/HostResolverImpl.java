package com.polaris.gateway;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.littleshoot.proxy.HostResolver;

import com.github.pagehelper.util.StringUtil;
import com.polaris.comm.Constant;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.config.ConfListener;
import com.polaris.comm.config.ConfigHandlerProvider;
import com.polaris.core.connect.ServerDiscoveryHandlerProvider;

/**
 * @author:Tom.Yu Description:
 */
public class HostResolverImpl implements HostResolver {

    private volatile static HostResolverImpl singleton;
    private volatile Map<String, String> serverMap = new ConcurrentHashMap<>();
    private volatile Map<String, String> staticServerMap = new ConcurrentHashMap<>();
    private volatile Map<String, String> uriMap = new ConcurrentHashMap<>();
    public static final String UPSTREAM = "upstream.txt";
    private static final String STATIC_RESOURCE_PREFIX = "static:";

    //载入需要代理的IP(需要动态代理)
    private void loadUpstream(String content) {
        if (StringUtil.isEmpty(content)) {
            return;
        }
        Map<String, String> tempStaticServerMap = new ConcurrentHashMap<>();
        Map<String, String> tempServerMap = new ConcurrentHashMap<>();
        Map<String, String> tempUriMap = new ConcurrentHashMap<>();
        String[] contents = content.split(Constant.LINE_SEP);
        int port = 7000;
        for (String detail : contents) {
        	detail = detail.replace("\n", "");
        	detail = detail.replace("\r", "");
            String[] keyvalue = ConfigHandlerProvider.getKeyValue(detail);
            if (keyvalue != null) {
                tempServerMap.put(String.valueOf(port), keyvalue[1]);
                if (keyvalue[0].startsWith(STATIC_RESOURCE_PREFIX)) {
                	String key = keyvalue[0].substring(STATIC_RESOURCE_PREFIX.length());
                	tempStaticServerMap.put(key, key);
                    tempUriMap.put(key, String.valueOf(port));
                } else {
                    tempUriMap.put(keyvalue[0], String.valueOf(port));
                }
                port++;
            }
        }
        staticServerMap = tempStaticServerMap;
        serverMap = tempServerMap;
        uriMap = tempUriMap;
        ServerDiscoveryHandlerProvider.getInstance().reset();
    }

    //构造函数（单例）
    private HostResolverImpl() {
       
    	//先获取
    	loadUpstream(ConfClient.getConfigValue(UPSTREAM));
    	
    	//后监听
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

        // default
        if (uriMap.containsKey(GatewayConstant.DEFAULT)) {
            return uriMap.get(GatewayConstant.DEFAULT);
        }

        //异常
        throw new NullPointerException("url is null");
    }

    @Override
    public InetSocketAddress resolve(String host, int port)
            throws UnknownHostException {
        String defaultUri = ServerDiscoveryHandlerProvider.getInstance().getUrl(serverMap.get(uriMap.get(GatewayConstant.DEFAULT)));
        String key = String.valueOf(port);
        if (serverMap.containsKey(key)) {
            String uri = ServerDiscoveryHandlerProvider.getInstance().getUrl(serverMap.get(key));
            if (StringUtil.isNotEmpty(uri)) {

                String[] si = uri.split(":");
                return new InetSocketAddress(si[0], Integer.parseInt(si[1]));
            } else {
                String[] si = defaultUri.split(":");
                return new InetSocketAddress(si[0], Integer.parseInt(si[1]));
            }
        } else {
            String[] si = defaultUri.split(":");
            return new InetSocketAddress(si[0], Integer.parseInt(si[1]));
        }
    }
    
    //是否是静态资源（静态资源不需要拦截）
    public boolean isStatic(String url) {
    	if (StringUtil.isEmpty(url)) {
    		return false;
    	}
    	for (String key : staticServerMap.keySet()) {
    		if (url.startsWith(key)) {
    			return true;
    		}
    	}
    	return false;
    }
}
