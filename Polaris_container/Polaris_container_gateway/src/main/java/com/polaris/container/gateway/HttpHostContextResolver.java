package com.polaris.container.gateway;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.polaris.container.gateway.pojo.HttpProxy;
import com.polaris.container.gateway.pojo.HttpUpstream;
import com.polaris.container.gateway.proxy.HostResolver;
import com.polaris.core.naming.NamingClient;
import com.polaris.core.pojo.Server;
import com.polaris.core.util.StringUtil;

/**
 * @author:Tom.Yu Description:
 */
public class HttpHostContextResolver implements HostResolver{
    public static HttpHostContextResolver INSTANCE = new HttpHostContextResolver();
    private HttpHostContextResolver() {
    }


    @Override
    public InetSocketAddress resolve(HttpProxy httpServerFile)
            throws UnknownHostException {
        if (httpServerFile != null) {
            String proxy = httpServerFile.getProxy();
            if (StringUtil.isNotEmpty(proxy)) {
                
                //upstream
                HttpUpstream upstream = HttpUpstream.getUpstreamMap().get(proxy);
                if (upstream != null) {
                    proxy = upstream.getHost();
                }
                
                //namingCenter
                Server server = NamingClient.getServer(proxy);
                if (server != null) {
                    return new InetSocketAddress(server.getIp(), server.getPort());
                } 
            }
        }
        throw new UnknownHostException(httpServerFile.getProxy() + httpServerFile.getRewrite());
    }
}
