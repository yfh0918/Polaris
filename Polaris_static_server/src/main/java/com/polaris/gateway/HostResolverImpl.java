package com.polaris.gateway;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.littleshoot.proxy.HostResolver;

/**
 * @author:Tom.Yu Description:
 */
public class HostResolverImpl implements HostResolver {

    private volatile static HostResolverImpl singleton;

    //构造函数（单例）
    private HostResolverImpl() {
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
    public InetSocketAddress resolve(String host, int port)
            throws UnknownHostException {
    	return new InetSocketAddress(host, port);
    }
}
