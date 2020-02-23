package com.polaris.container.gateway;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.littleshoot.proxy.HostResolver;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.config.ConfHandlerProvider;
import com.polaris.core.config.ConfHandlerSupport;
import com.polaris.core.config.ConfListener;
import com.polaris.core.naming.ServerHandlerProvider;
import com.polaris.core.naming.ServerHandlerSupport;
import com.polaris.core.util.StringUtil;

import cn.hutool.core.collection.ConcurrentHashSet;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu Description:
 */
public class HostResolverImpl implements HostResolver {

    private volatile static HostResolverImpl singleton;
    private volatile Map<String, String> portServerMap = new ConcurrentHashMap<>();
    private volatile Set<String> staticServerSet = new ConcurrentHashSet<>();
    private volatile Map<String, String> uriPortMap = new ConcurrentHashMap<>();
    public static final String UPSTREAM = "upstream.txt";
    private static final String STATIC_RESOURCE_PREFIX = "static:";

    //载入需要代理的IP(需要动态代理)
    private void loadUpstream(String content) {
        if (StringUtil.isEmpty(content)) {
            return;
        }
        Set<String> tempStaticServerSet = new ConcurrentHashSet<>();
        Map<String, String> tempPortServerMap = new ConcurrentHashMap<>();
        Map<String, String> tempUriPortMap = new ConcurrentHashMap<>();
        String[] contents = content.split(Constant.LINE_SEP);
        int port = 7000;
        for (String detail : contents) {
        	detail = detail.replace("\n", "");
        	detail = detail.replace("\r", "");
            String[] contextUrl = ConfHandlerSupport.getKeyValue(detail);
            
            //获取context和服务名称(url)
            if (contextUrl != null) {
            	
            	//key=端口号，value=服务
            	tempPortServerMap.put(String.valueOf(port), contextUrl[1]);
            	
            	//是否包含[static:]
                if (contextUrl[0].startsWith(STATIC_RESOURCE_PREFIX)) {
                	String key = contextUrl[0].substring(STATIC_RESOURCE_PREFIX.length());
                	tempStaticServerSet.add(key);//记录今天服务的set
                	tempUriPortMap.put(key, String.valueOf(port)); //key = context, value = 端口号
                } else {
                	tempUriPortMap.put(contextUrl[0], String.valueOf(port)); //key = context, value = 端口号
                }
                
                //端口号累加（临时的端口号）
                port++;
            }
        }
        staticServerSet = tempStaticServerSet;
        portServerMap = tempPortServerMap;
        uriPortMap = tempUriPortMap;
        ServerHandlerSupport.reset();
    }

    //构造函数（单例）
    private HostResolverImpl() {
       
    	//先获取
    	loadUpstream(ConfHandlerProvider.INSTANCE.get(UPSTREAM));
    	
    	//后监听
    	ConfHandlerProvider.INSTANCE.listen(UPSTREAM, new ConfListener() {
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
        return portServerMap.get(key);
    }

    //根据服务，反向获取端口号
    String getPort(String uri) {
    	if (!uri.substring(1).contains("/")) {
    		uri = uri + "/";
    	}
        if (uri != null) {
            for (Entry<String, String> entry : uriPortMap.entrySet()) {
                if (uri.startsWith(entry.getKey()+"/")) {
                    return entry.getValue();
                }

            }
        }

        // default
        if (uriPortMap.containsKey(GatewayConstant.DEFAULT)) {
            return uriPortMap.get(GatewayConstant.DEFAULT);
        }

        //异常
        throw new NullPointerException("url is null");
    }
    
    //替换host 通过uri可见的端口号替换成 uriPortMap中的端口号
    void replaceHost(HttpRequest httpRequest) {
    	
    	//获取HOST
    	String host = httpRequest.headers().get(GatewayConstant.HOST);
    	if (!host.contains(":")) {
    		host = host + ":" + ConfClient.get("server.port", "80");
    	}
    	
    	//获取HOST中的的端口号
    	String oldPort = host.substring(host.indexOf(":") + 1);
    	
    	//获取URI中新的端口号
		String uri = httpRequest.uri();
		String port = HostResolverImpl.getSingleton().getPort(uri);
		
		//移除就得HOST
		httpRequest.headers().remove(GatewayConstant.HOST);
		
		//添加新的HOST
		httpRequest.headers().add(GatewayConstant.HOST, host.replace(oldPort, port));
    }
    //replaceHost的反操作
    void resetHost(HttpRequest httpRequest) {
		String host = httpRequest.headers().get(GatewayConstant.HOST);
    	String oldPort = host.substring(host.indexOf(":") + 1);
		httpRequest.headers().remove(GatewayConstant.HOST);
		httpRequest.headers().add(GatewayConstant.HOST, host.replace(oldPort, ConfClient.get("server.port","80")));
    }

    @Override
    public InetSocketAddress resolve(String host, int port)
            throws UnknownHostException {
    	
    	//端口号
        String strPort = String.valueOf(port);
        String[] si = null;
        
        //存在端口号
        if (portServerMap.containsKey(strPort)) {
            String uri = ServerHandlerProvider.getInstance().getUrl(portServerMap.get(strPort));
            if (StringUtil.isNotEmpty(uri)) {
                si = uri.split(":");
            } 
        }
        if (si == null) {
        	String defaultUri = ServerHandlerProvider.getInstance().getUrl(portServerMap.get(uriPortMap.get(GatewayConstant.DEFAULT)));
            si = defaultUri.split(":");
        }
        
        //返回地址列表
        if (si.length == 1) {
            return new InetSocketAddress(si[0], 80);
        }
        return new InetSocketAddress(si[0], Integer.parseInt(si[1]));
    }
    
    //是否是静态资源（静态资源不需要拦截）
    public boolean isStatic(String url) {
    	if (StringUtil.isEmpty(url)) {
    		return false;
    	}
    	for (String key : staticServerSet) {
    		if (url.startsWith(key)) {
    			return true;
    		}
    	}
    	return false;
    }
}
