package com.polaris.container.gateway;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.polaris.container.gateway.pojo.HostUpstream;
import com.polaris.container.gateway.proxy.HostResolver;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.provider.ConfHandlerProviderFactory;
import com.polaris.core.naming.provider.ServerStrategyProviderFactory;
import com.polaris.core.util.StringUtil;

import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu Description:
 */
public class HostResolverImpl implements HostResolver {
    private volatile static HostResolverImpl singleton;

    private void loadUpstream(String content) {
        if (StringUtil.isEmpty(content)) {
            return;
        }
        String[] contents = content.split(Constant.LINE_SEP);
        HostUpstream.load(contents);
        ServerStrategyProviderFactory.get().init();
    }

    private HostResolverImpl() {
       
    	//先获取
    	loadUpstream(ConfHandlerProviderFactory.get(Type.EXT).get(HostUpstream.NAME));
    	
    	//后监听
    	ConfHandlerProviderFactory.get(Type.EXT).listen(HostUpstream.NAME, new ConfHandlerListener() {
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

    @Override
    public InetSocketAddress resolve(String host, int virtualPort,HttpRequest originalRequest)
            throws UnknownHostException {
    	
    	//元素0:ip  元素1:port
        String[] address = null;

        //端口号
    	HostUpstream upstream = HostUpstream.getFromUri(originalRequest.uri());
        if (upstream != null) {
            String uri = ServerStrategyProviderFactory.get().getUrl(upstream.getHost());
            if (StringUtil.isNotEmpty(uri)) {
                address = uri.split(GatewayConstant.COLON);
            } 
        }
        if (address == null) {
        	upstream = HostUpstream.getFromContext(GatewayConstant.DEFAULT);
        	if (upstream != null) {
        		String uri = ServerStrategyProviderFactory.get().getUrl(upstream.getHost());
        		if (StringUtil.isNotEmpty(uri)) {
                    address = uri.split(GatewayConstant.COLON);
                } 
        	}
        }
        if (address != null) {
            if (address.length == 1) {
                return new InetSocketAddress(address[0], 80);
            }
            return new InetSocketAddress(address[0], Integer.parseInt(address[1]));
        }
        throw new UnknownHostException(host + GatewayConstant.COLON + virtualPort);
    }
}
