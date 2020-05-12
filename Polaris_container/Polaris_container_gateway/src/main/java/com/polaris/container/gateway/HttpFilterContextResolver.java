package com.polaris.container.gateway;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.polaris.container.gateway.pojo.HttpFilterFile;
import com.polaris.container.gateway.pojo.HttpUpstream;
import com.polaris.container.gateway.proxy.HostResolver;
import com.polaris.core.naming.provider.ServerStrategyProviderFactory;
import com.polaris.core.pojo.Server;

/**
 * @author:Tom.Yu Description:
 */
public class HttpFilterContextResolver implements HostResolver ,HttpFilterFileListener{
    public static HttpFilterContextResolver INSTANCE = new HttpFilterContextResolver();
    private HttpFilterContextResolver() {
    	HttpFilterFileReader.INSTANCE.readFile(this, new HttpFilterFile(HttpUpstream.NAME));
    }
    
    /**
     * 配置更新回调
     *
     */
    @Override
    public void onChange(HttpFilterFile file) {
        HttpUpstream.load(file.getData());
    }

    @Override
    public InetSocketAddress resolve(String host, int port, String context)
            throws UnknownHostException {
    	HttpUpstream upstream = HttpUpstream.getFromContext(context);
        if (upstream != null) {
            Server server = ServerStrategyProviderFactory.get().getServer(upstream.getServiceName());
            if (server != null) {
                return new InetSocketAddress(server.getIp(), server.getPort());
            } 
        }
        throw new UnknownHostException(host + HttpFilterConstant.COLON + port + context);
    }
}
