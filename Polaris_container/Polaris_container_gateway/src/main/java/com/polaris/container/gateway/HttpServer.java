package com.polaris.container.gateway;

import java.net.InetSocketAddress;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.container.config.ConfigurationHelper;
import com.polaris.container.gateway.proxy.ActivityTrackerAdapter;
import com.polaris.container.gateway.proxy.FlowContext;
import com.polaris.container.gateway.proxy.HttpFilters;
import com.polaris.container.gateway.proxy.HttpFiltersSourceAdapter;
import com.polaris.container.gateway.proxy.HttpProxyServerBootstrap;
import com.polaris.container.gateway.proxy.extras.SelfSignedSslEngineSourceExt;
import com.polaris.container.gateway.proxy.impl.DefaultHttpProxyServer;
import com.polaris.container.gateway.proxy.impl.ThreadPoolConfiguration;
import com.polaris.container.util.NetUtils;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.SpringUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class HttpServer {
	
	private static Logger logger = LoggerFactory.getLogger(HttpServer.class);
	
	private InetSocketAddress inetSocketAddress;
	
	/**
     * 服务器
     */
    private HttpProxyServerBootstrap httpProxyServerBootstrap = null;
    
	/**
     * 私有构造方法
     */
    private HttpServer() {
    }
    
    /**
     * 获取单实例公共静态方法
     *
     * @return 单实例
     */
    public static HttpServer getInstance() {
        return Singletone.INSTANCE;
    }

    /**
     * 静态内部类实现单例
     */
    private static class Singletone {
        /**
         * 单实例
         */
        private static final HttpServer INSTANCE = new HttpServer();
    }
    
    /**
     * 启动服务器
     *
     * @throws Exception
     */
    public void start() {

    	//创建context
    	SpringUtil.refresh(ConfigurationHelper.getConfiguration());
        ThreadPoolConfiguration threadPoolConfiguration = new ThreadPoolConfiguration();
        threadPoolConfiguration.withAcceptorThreads(HttpConstant.AcceptorThreads);
        threadPoolConfiguration.withClientToProxyWorkerThreads(HttpConstant.ClientToProxyWorkerThreads);
        threadPoolConfiguration.withProxyToServerWorkerThreads(HttpConstant.ProxyToServerWorkerThreads);

        inetSocketAddress = new InetSocketAddress(Integer.parseInt(ConfClient.get("server.port")));
        httpProxyServerBootstrap = DefaultHttpProxyServer.bootstrap()
                .withAddress(inetSocketAddress);
        httpProxyServerBootstrap.withServerResolver(HttpResolverFactory.get());
        boolean proxy_tls = HttpConstant.ON.equals(ConfClient.get("server.tls"));
        if (proxy_tls) {
            logger.info("开启TLS支持");
            httpProxyServerBootstrap
                    //不验证client端证书
                    .withAuthenticateSslClients(false)
                    .withSslEngineSource(new SelfSignedSslEngineSourceExt());
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
                        List<String> headerValues = HttpConstant.getHeaderValues(httpRequest, HttpConstant.X_Real_IP);
                        List<String> headerValues2 = HttpConstant.getHeaderValues(httpRequest, HttpConstant.X_Forwarded_For);
                        if (headerValues.size() == 0) {
                        	if (headerValues2 != null && headerValues2.size() > 0) {
                        		httpRequest.headers().add(HttpConstant.X_Real_IP, headerValues2.get(0));
                        	} else {
                        		String remoteAddress = flowContext.getClientAddress().getAddress().getHostAddress();
                                httpRequest.headers().add(HttpConstant.X_Real_IP, remoteAddress);
                        	}
                        }

                        //设置XFF
                        StringBuilder xff = new StringBuilder();
                        if (headerValues2.size() > 0 && headerValues2.get(0) != null) {
                            //逗号面一定要带一个空格
                            xff.append(headerValues2.get(0)).append(", ");
                        }
                        xff.append(NetUtils.getLocalHost());
                        httpRequest.headers().set(HttpConstant.X_Forwarded_For, xff.toString());
                    }
                })
                .withFiltersSource(new HttpFiltersSourceAdapter() {
                    @Override
                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                        return new HttpFilterAdapterImpl(originalRequest, ctx);
                    }
                }).start();
        logger.info("Gateway started on port(s) " + inetSocketAddress.getPort() + " with context path '/'");
    }
    
    /**
     * 停止服务服务器
     *
     * @throws Exception
     */
    public void stop() {
        ConfigurableApplicationContext context = SpringUtil.getApplicationContext();
        if (context != null) {
           context.close();
        }
       	httpProxyServerBootstrap.abort();//non graceful
    }

}
