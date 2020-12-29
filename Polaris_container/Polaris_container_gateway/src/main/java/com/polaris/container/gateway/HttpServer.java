package com.polaris.container.gateway;

import java.net.InetSocketAddress;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.polaris.container.ServerOrder;
import com.polaris.container.SpringContextServer;
import com.polaris.container.gateway.pojo.HttpProtocolForConnection;
import com.polaris.container.gateway.proxy.ActivityTrackerAdapter;
import com.polaris.container.gateway.proxy.FlowContext;
import com.polaris.container.gateway.proxy.HttpFilters;
import com.polaris.container.gateway.proxy.HttpFiltersSourceAdapter;
import com.polaris.container.gateway.proxy.HttpProxyServerBootstrap;
import com.polaris.container.gateway.proxy.impl.DefaultHttpProxyServer;
import com.polaris.container.gateway.proxy.impl.ThreadPoolConfiguration;
import com.polaris.container.gateway.proxy.tls.SslEngineSourceFactory;
import com.polaris.container.util.NetUtils;
import com.polaris.core.config.ConfClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

@Order(ServerOrder.GATEWAY)
public class HttpServer extends SpringContextServer{
	
	private static Logger logger = LoggerFactory.getLogger(HttpServer.class);
	
	/**
     * 服务器
     */
    private HttpProxyServerBootstrap httpProxyServerBootstrap = null;
    
    /**
     * 启动服务器
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception{
    	super.start();
    	
        //milliseconds -
        int timeout = HttpProtocolForConnection.getTimeout();
        int idleConnectionTimeout = HttpProtocolForConnection.getIdleTimeout();

        //start
        ThreadPoolConfiguration threadPoolConfiguration = new ThreadPoolConfiguration();
        threadPoolConfiguration.withAcceptorThreads(HttpConstant.AcceptorThreads);
        threadPoolConfiguration.withClientToProxyWorkerThreads(HttpConstant.ClientToProxyWorkerThreads);
        threadPoolConfiguration.withProxyToServerWorkerThreads(HttpConstant.ProxyToServerWorkerThreads);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(Integer.parseInt(ConfClient.get("server.port")));
        httpProxyServerBootstrap = DefaultHttpProxyServer.bootstrap()
                .withAddress(inetSocketAddress);
        httpProxyServerBootstrap.withServerResolver(HttpResolverFactory.get())
                .withSslEngineSource(SslEngineSourceFactory.get())
                .withConnectTimeout(timeout)
                .withIdleConnectionTimeout(idleConnectionTimeout)
                .withThreadPoolConfiguration(threadPoolConfiguration)
                .withThrottling(HttpProtocolForConnection.getReadThrottleBytesPerSecond(), HttpProtocolForConnection.getWriteThrottleBytesPerSecond())
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
                        return HttpFilterAdapterFactory.create(originalRequest, ctx);
                    }
                }).start();
        logger.info("Gateway started on port(s) " + inetSocketAddress.getPort() + " with context path '/'");
    }
    
    /**
     * 停止服务服务器
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        super.stop();
       	httpProxyServerBootstrap.stop();//graceful shutdown
    }

}
