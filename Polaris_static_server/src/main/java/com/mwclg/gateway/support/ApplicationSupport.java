package com.mwclg.gateway.support;

import java.net.InetSocketAddress;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.littleshoot.proxy.impl.ThreadPoolConfiguration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mwclg.gateway.GatewayConstant;
import com.mwclg.gateway.HostResolverImpl;
import com.mwclg.gateway.HttpFilterAdapterImpl;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.connect.ServerDiscoveryHandlerProvider;
import com.polaris.core.util.SpringUtil;
import com.polaris.http.util.NetUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class ApplicationSupport {
	
    //启动网关应用
    @SuppressWarnings("resource")
	public static void startGateway() {
    	
    	//载入参数
    	ConfClient.init();

    	//载入spring.xml
    	new ClassPathXmlApplicationContext(SpringUtil.SPRING_PATH);
    	
    	//注册服务
		if (Constant.SWITCH_ON.equals(ConfClient.get(Constant.NAME_REGISTRY_SWITCH, Constant.SWITCH_ON))) {
			ServerDiscoveryHandlerProvider.getInstance().register(NetUtils.getLocalHost(), Integer.parseInt(ConfClient.get(Constant.SERVER_PORT_NAME)));
			
			// add shutdown hook to stop server
	        Runtime.getRuntime().addShutdownHook(new Thread() {
	            public void run() {
	            	ServerDiscoveryHandlerProvider.getInstance().deregister(NetUtils.getLocalHost(), Integer.parseInt(ConfClient.get(Constant.SERVER_PORT_NAME)));
	            }
	        });
		}
    			
        ThreadPoolConfiguration threadPoolConfiguration = new ThreadPoolConfiguration();
        threadPoolConfiguration.withAcceptorThreads(GatewayConstant.AcceptorThreads);
        threadPoolConfiguration.withClientToProxyWorkerThreads(GatewayConstant.ClientToProxyWorkerThreads);
        threadPoolConfiguration.withProxyToServerWorkerThreads(GatewayConstant.ProxyToServerWorkerThreads);

        InetSocketAddress inetSocketAddress = new InetSocketAddress(Integer.parseInt(ConfClient.get("server.port")));
        HttpProxyServerBootstrap httpProxyServerBootstrap = DefaultHttpProxyServer.bootstrap()
                .withAddress(inetSocketAddress);
        
        //反向代理模式
        httpProxyServerBootstrap.withServerResolver(HostResolverImpl.getSingleton());
        httpProxyServerBootstrap.withAllowRequestToOriginServer(true)
                .withThreadPoolConfiguration(threadPoolConfiguration)
                .withFiltersSource(new HttpFiltersSourceAdapter() {
                    @Override
                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                        return new HttpFilterAdapterImpl(originalRequest, ctx);
                    }
                }).start();
    }
    
}
