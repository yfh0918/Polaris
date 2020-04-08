package com.polaris.container.gateway;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map.Entry;

import org.littleshoot.proxy.HostResolver;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.provider.ConfHandlerProviderFactory;
import com.polaris.core.naming.provider.ServerStrategyProviderFactory;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.StringUtil;

import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu Description:
 */
public class HostResolverImpl implements HostResolver {

    private volatile static HostResolverImpl singleton;
    
    private static String port =  ConfClient.get("server.port", "80");

    //载入需要代理的IP(需要动态代理)
    private void loadUpstream(String content) {
        if (StringUtil.isEmpty(content)) {
            return;
        }
        String[] contents = content.split(Constant.LINE_SEP);
        Upstream.load(contents);
        ServerStrategyProviderFactory.get().init();
    }

    //构造函数（单例）
    private HostResolverImpl() {
       
    	//先获取
    	loadUpstream(ConfHandlerProviderFactory.get(Type.EXT).get(Upstream.NAME));
    	
    	//后监听
    	ConfHandlerProviderFactory.get(Type.EXT).listen(Upstream.NAME, new ConfHandlerListener() {
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

    public String getHostFromVirtualPort(String key) {
        return Upstream.getFromVirtualPort(key).getHost();
    }

    //根据服务，反向获取端口号
    public String getVirtualPort(String uri) {
    	if (!uri.substring(1).contains("/")) {
    		uri = uri + "/";
    	}
        if (uri != null) {
            for (Entry<String, Upstream> entry : Upstream.getContextEntrySet()) {
                if (uri.startsWith(entry.getKey()+"/")) {
                    return entry.getValue().getVirtualPort();
                }
            }
        }

        // default
        Upstream upstream = Upstream.getFromContext(GatewayConstant.DEFAULT);
        if (upstream != null) {
        	return upstream.getVirtualPort();
        }

        //异常
        throw new NullPointerException("url is not corrected");
    }
    
    public void convertHost(HttpRequest httpRequest) {
    	String host = ConfClient.get(Constant.IP_ADDRESS, NetUtils.getLocalHost()) + ":" + port;
		String virtualPort = getVirtualPort(httpRequest.uri());
		httpRequest.headers().remove(GatewayConstant.HOST);
		httpRequest.headers().add(GatewayConstant.HOST, host.replace(port, virtualPort));
    }
    
    public void reConvertHost(HttpRequest httpRequest) {
		String host = httpRequest.headers().get(GatewayConstant.HOST);
    	String virtualPort = host.substring(host.indexOf(":") + 1);
		httpRequest.headers().remove(GatewayConstant.HOST);
		httpRequest.headers().add(GatewayConstant.HOST, host.replace(virtualPort, port));
    }

    @Override
    public InetSocketAddress resolve(String host, int virtualPort)
            throws UnknownHostException {
    	
    	//元素0:ip  元素1:port
        String[] address = null;

        //端口号
    	Upstream upstream = Upstream.getFromVirtualPort(String.valueOf(virtualPort));
        if (upstream != null) {
            String uri = ServerStrategyProviderFactory.get().getUrl(upstream.getHost());
            if (StringUtil.isNotEmpty(uri)) {
                address = uri.split(":");
            } 
        }
        if (address == null) {
        	upstream = Upstream.getFromContext(GatewayConstant.DEFAULT);
        	if (upstream != null) {
        		String uri = ServerStrategyProviderFactory.get().getUrl(upstream.getHost());
        		if (StringUtil.isNotEmpty(uri)) {
                    address = uri.split(":");
                } 
        	}
        }
        if (address != null) {
            if (address.length == 1) {
                return new InetSocketAddress(address[0], 80);
            }
            return new InetSocketAddress(address[0], Integer.parseInt(address[1]));
        }
        throw new UnknownHostException(host + ":" + virtualPort);
    }
}
