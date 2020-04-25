package com.polaris.container.gateway;

import java.net.InetSocketAddress;
import java.util.List;

import org.littleshoot.proxy.ActivityTrackerAdapter;
import org.littleshoot.proxy.FlowContext;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.littleshoot.proxy.impl.ThreadPoolConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.config.ConfigurationSupport;
import com.polaris.container.gateway.util.SelfSignedSslEngineSource;
import com.polaris.container.listener.ServerListenerSupport;
import com.polaris.container.util.NetUtils;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.SpringUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class MainServer {
	
	private static Logger logger = LoggerFactory.getLogger(MainServer.class);
	
	/**
     * 服务器
     */
    private HttpProxyServerBootstrap httpProxyServerBootstrap = null;
    
	/**
     * 私有构造方法
     */
    private MainServer() {
    }
    
    /**
     * 获取单实例公共静态方法
     *
     * @return 单实例
     */
    public static MainServer getInstance() {
        return Singletone.INSTANCE;
    }

    /**
     * 静态内部类实现单例
     */
    private static class Singletone {
        /**
         * 单实例
         */
        private static final MainServer INSTANCE = new MainServer();
    }
    
    /**
     * 启动服务器
     *
     * @throws Exception
     */
    public void start() {

    	//创建context
    	SpringUtil.refresh(ConfigurationSupport.getConfiguration());
        ThreadPoolConfiguration threadPoolConfiguration = new ThreadPoolConfiguration();
        threadPoolConfiguration.withAcceptorThreads(GatewayConstant.AcceptorThreads);
        threadPoolConfiguration.withClientToProxyWorkerThreads(GatewayConstant.ClientToProxyWorkerThreads);
        threadPoolConfiguration.withProxyToServerWorkerThreads(GatewayConstant.ProxyToServerWorkerThreads);

        InetSocketAddress inetSocketAddress = new InetSocketAddress(Integer.parseInt(ConfClient.get("server.port")));
        httpProxyServerBootstrap = DefaultHttpProxyServer.bootstrap()
                .withAddress(inetSocketAddress);
        boolean proxy_tls = GatewayConstant.ON.equals(ConfClient.get("server.tls"));
        
        //反向代理模式
        logger.info("反向代理模式开启");
        httpProxyServerBootstrap.withServerResolver(HostResolverImpl.getSingleton());

        if (proxy_tls) {
            logger.info("开启TLS支持");
            httpProxyServerBootstrap
                    //不验证client端证书
                    .withAuthenticateSslClients(false)
                    .withSslEngineSource(new SelfSignedSslEngineSource());
        } 
        //milliseconds - 40seconds
        int timeout = Integer.parseInt(ConfClient.get("connect.timeout","40000"));
        httpProxyServerBootstrap.withConnectTimeout(timeout);        
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
        HttpFilterHelper.init();//初始化过滤器
        ServerListenerSupport.started();//监听启动
        logger.info("Gateway started on port(s) " + inetSocketAddress.getPort() + " with context path '/'");
        
        // add shutdown hook to stop server
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                	ServerListenerSupport.stopped();
                	logger.info("Gateway stopped on port(s) " + inetSocketAddress.getPort() + " with context path '/'");
                } catch (Exception e) {
                    logger.error("failed to stop gateway.", e);
                }
            }
        });
    	
    }
}
