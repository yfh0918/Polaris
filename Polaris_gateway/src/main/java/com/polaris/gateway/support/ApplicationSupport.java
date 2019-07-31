package com.polaris.gateway.support;

import java.net.InetSocketAddress;
import java.util.List;

import org.littleshoot.proxy.ActivityTrackerAdapter;
import org.littleshoot.proxy.FlowContext;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.littleshoot.proxy.impl.ThreadPoolConfiguration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.connect.ServerDiscoveryHandlerProvider;
import com.polaris.core.util.LogUtil;
import com.polaris.core.util.SpringUtil;
import com.polaris.gateway.GatewayConstant;
import com.polaris.gateway.HostResolverImpl;
import com.polaris.gateway.HttpFilterAdapterImpl;
import com.polaris.gateway.util.GatewaySelfSignedSslEngineSource;
import com.polaris.http.util.NetUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class ApplicationSupport {
	
	private static LogUtil logger = LogUtil.getInstance(ApplicationSupport.class);
	
    //启动网关应用
    @SuppressWarnings("resource")
	public static void startGateway() {
    	
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
        boolean proxy_tls = !GatewayConstant.OFF.equals(ConfClient.get("server.tls"));
        
        //反向代理模式
        logger.info("反向代理模式开启");
        httpProxyServerBootstrap.withServerResolver(HostResolverImpl.getSingleton());

        if (proxy_tls) {
            logger.info("开启TLS支持");
            httpProxyServerBootstrap
                    //不验证client端证书
                    .withAuthenticateSslClients(false)
                    .withSslEngineSource(new GatewaySelfSignedSslEngineSource());
        } 
        httpProxyServerBootstrap.withAllowRequestToOriginServer(true)
                .withProxyAlias(ConfClient.get("server.tls.alias"))
                .withThreadPoolConfiguration(threadPoolConfiguration)
                //X-Real-IP,XFF设置
                .plusActivityTracker(new ActivityTrackerAdapter() {
                    @Override
                    public void requestReceivedFromClient(FlowContext flowContext,
                                                          HttpRequest httpRequest) {

                    	//如何设置真实IP
                        List<String> headerValues = GatewayConstant.getHeaderValues(httpRequest, GatewayConstant.X_Real_IP);
                        List<String> headerValues2 = GatewayConstant.getHeaderValues(httpRequest, GatewayConstant.X_Forwarded_For);
                        if (headerValues.size() == 0) {
                        	if (headerValues2 != null && headerValues2.size() > 0) {
                        		httpRequest.headers().add(GatewayConstant.X_Real_IP, headerValues2.get(0));
                        	} else {
                        		String remoteAddress = flowContext.getClientAddress().getAddress().getHostAddress();
                                httpRequest.headers().add(GatewayConstant.X_Real_IP, remoteAddress);
                        	}
                        }

                        //设置XFF
                        StringBuilder xff = new StringBuilder();
                        if (headerValues2.size() > 0 && headerValues2.get(0) != null) {
                            //逗号面一定要带一个空格
                            xff.append(headerValues2.get(0)).append(", ");
                        }
                        xff.append(NetUtils.getLocalHost());
                        httpRequest.headers().set(GatewayConstant.X_Forwarded_For, xff.toString());
                    }
                })
                .withFiltersSource(new HttpFiltersSourceAdapter() {
                    @Override
                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                        return new HttpFilterAdapterImpl(originalRequest, ctx);
                    }
                }).start();
    }
    
}
