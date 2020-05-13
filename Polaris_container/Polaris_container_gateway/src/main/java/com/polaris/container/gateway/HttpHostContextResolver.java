package com.polaris.container.gateway;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.container.gateway.pojo.HttpHostContext;
import com.polaris.container.gateway.pojo.HttpHostContext.HttpContextUpstream;
import com.polaris.container.gateway.proxy.HostResolver;
import com.polaris.core.naming.provider.ServerStrategyProviderFactory;
import com.polaris.core.pojo.Server;

/**
 * @author:Tom.Yu Description:
 */
public class HttpHostContextResolver implements HostResolver ,HttpFileListener{
    public static HttpHostContextResolver INSTANCE = new HttpHostContextResolver();
    private HttpHostContextResolver() {
    	HttpFileReader.INSTANCE.readFile(this, new HttpFile(HttpHostContext.NAME));
    }
    
    /**
     * 配置更新回调
     *
     */
    @Override
    public void onChange(HttpFile file) {
        HttpHostContext.load(file.getData());
    }

    @Override
    public InetSocketAddress resolve(String host, int port, String context)
            throws UnknownHostException {
    	HttpContextUpstream upstream = HttpHostContext.get(host, context);
        if (upstream != null) {
            Server server = ServerStrategyProviderFactory.get().getServer(upstream.getServiceName());
            if (server != null) {
                return new InetSocketAddress(server.getIp(), server.getPort());
            } 
        }
        throw new UnknownHostException(host + HttpConstant.COLON + port + context);
    }
}
