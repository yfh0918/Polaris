package com.polaris.container.gateway;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.polaris.container.gateway.pojo.HostUpstream;
import com.polaris.container.gateway.pojo.HttpFilterFile;
import com.polaris.container.gateway.proxy.HostResolver;
import com.polaris.core.naming.provider.ServerStrategyProviderFactory;
import com.polaris.core.util.StringUtil;

/**
 * @author:Tom.Yu Description:
 */
public class HttpFilterHostResolver implements HttpFilterLifeCycle, HostResolver ,HttpFilterCallback{
    public static HttpFilterHostResolver INSTANCE = new HttpFilterHostResolver();
    private HttpFilterHostResolver() {}
    
    /**
     * 启动HostResolver
     *
     */
	@Override
	public void start() {
    	HttpFilterFileReader.INSTANCE.readFile(this, new HttpFilterFile(HostUpstream.NAME));
	}

    /**
     * 配置更新回调
     *
     */
    @Override
    public void onChange(HttpFilterFile file) {
        HostUpstream.create(file.getData());
        ServerStrategyProviderFactory.get().init();
    }

    @Override
    public InetSocketAddress resolve(String host, int port, String context)
            throws UnknownHostException {
    	
    	//元素0:ip  元素1:port
        String[] address = null;

        //端口号
    	HostUpstream upstream = HostUpstream.getFromContext(context);
        if (upstream != null) {
            String url = ServerStrategyProviderFactory.get().getUrl(upstream.getHost());
            if (StringUtil.isNotEmpty(url)) {
                address = url.split(HttpFilterConstant.COLON);
                if (address.length == 1) {
                    return new InetSocketAddress(address[0], 80);
                }
                return new InetSocketAddress(address[0], Integer.parseInt(address[1]));
            } 
        }
        throw new UnknownHostException(host + HttpFilterConstant.COLON + port);
    }
}
