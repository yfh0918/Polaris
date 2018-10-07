package com.polaris.gateway.support;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.littleshoot.proxy.ActivityTrackerAdapter;
import org.littleshoot.proxy.FlowContext;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.littleshoot.proxy.impl.ThreadPoolConfiguration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.polaris.comm.Constant;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.supports.MainSupport;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.PropertyUtils;
import com.polaris.comm.util.SpringUtil;
import com.polaris.gateway.GatewayConstant;
import com.polaris.gateway.HostResolverImpl;
import com.polaris.gateway.HttpFilterAdapterImpl;
import com.polaris.gateway.util.GatewaySelfSignedSslEngineSource;
import com.polaris.http.util.NetUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class ApplicationSupport {
	
	private static LogUtil logger = LogUtil.getInstance(ApplicationSupport.class);
	
    //启动线程监控日志，upsteam文件变化
    public static void configureAndWatch(long warchTime){
    	Thread run = new Thread(new Runnable(){
    		
    		 @Override  
             public void run() { 
    			 
    			long lastModified = 0l;
				//动态监测
                 while(true){  
                	try {
						long tempLastModified = new File(PropertyUtils.getFilePath(Constant.CONFIG + File.separator + Constant.LOG4J)).lastModified();
						
						//修改日志
						if (lastModified != tempLastModified) {
							lastModified = tempLastModified;
							PropertyConfigurator.configure(MainSupport.class.getClassLoader().getResourceAsStream(Constant.CONFIG + File.separator + Constant.LOG4J));
						}
						
						Thread.sleep(warchTime);
					} catch (Exception e) {
						logger.error(e);
					}
                 }  
             }  
    	});
    	run.setDaemon(true);//守护线程
    	run.setName("ConfigureAndWatch Thread");
    	run.start();
    }
    
    //启动网关应用
    @SuppressWarnings("resource")
	public static void startGateway() {
    	
    	MainSupport.iniParameter();
    	MainSupport.configureAndWatch(60000);
    	new ClassPathXmlApplicationContext(SpringUtil.SPRING_PATH);
    	
        ThreadPoolConfiguration threadPoolConfiguration = new ThreadPoolConfiguration();
        threadPoolConfiguration.withAcceptorThreads(GatewayConstant.AcceptorThreads);
        threadPoolConfiguration.withClientToProxyWorkerThreads(GatewayConstant.ClientToProxyWorkerThreads);
        threadPoolConfiguration.withProxyToServerWorkerThreads(GatewayConstant.ProxyToServerWorkerThreads);

        InetSocketAddress inetSocketAddress = new InetSocketAddress(Integer.parseInt(GatewayConstant.SERVER_PORT));
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
                .withProxyAlias(GatewaySelfSignedSslEngineSource.ALIAS)
                .withThreadPoolConfiguration(threadPoolConfiguration)
                //XFF设置
                .plusActivityTracker(new ActivityTrackerAdapter() {
                    @Override
                    public void requestReceivedFromClient(FlowContext flowContext,
                                                          HttpRequest httpRequest) {

                        String xffKey = GatewayConstant.X_Forwarded_For;
                        StringBuilder xff = new StringBuilder();
                        List<String> headerValues1 = GatewayConstant.getHeaderValues(httpRequest, xffKey);
                        if (headerValues1.size() > 0 && headerValues1.get(0) != null) {
                            //逗号面一定要带一个空格
                            xff.append(headerValues1.get(0)).append(", ");
                        }
                        xff.append(NetUtils.getLocalHost());
                        httpRequest.headers().set(xffKey, xff.toString());
                    }
                })
                //X-Real-IP设置
                .plusActivityTracker(
                        new ActivityTrackerAdapter() {
                            @Override
                            public void requestReceivedFromClient(FlowContext flowContext,
                                                                  HttpRequest httpRequest) {
                                List<String> headerValues2 = GatewayConstant.getHeaderValues(httpRequest, GatewayConstant.X_Real_IP);
                                if (headerValues2.size() == 0) {
                                    String remoteAddress = flowContext.getClientAddress().getAddress().getHostAddress();
                                    httpRequest.headers().add(GatewayConstant.X_Real_IP, remoteAddress);
                                }
                            }
                        }
                )
                .withFiltersSource(new HttpFiltersSourceAdapter() {
                    @Override
                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                        return new HttpFilterAdapterImpl(originalRequest, ctx);
                    }
                }).start();
    }
    
}
