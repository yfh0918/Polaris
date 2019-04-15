package com.polaris.gateway;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.impl.ClientToProxyConnection;
import org.littleshoot.proxy.impl.ProxyToServerConnection;

import com.polaris.comm.Constant;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.dto.ResultDto;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.UuidUtil;
import com.polaris.core.connect.ServerDiscoveryHandlerProvider;
import com.polaris.gateway.request.HttpRequestFilter;
import com.polaris.gateway.request.HttpRequestFilterChain;
import com.polaris.gateway.response.HttpResponseFilterChain;
import com.polaris.gateway.support.HttpRequestFilterSupport;
import com.polaris.gateway.util.RequestUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class HttpFilterAdapterImpl extends HttpFiltersAdapter {
	private static LogUtil logger = LogUtil.getInstance(HttpFilterAdapterImpl.class);

    //构造过滤器适配器
    public HttpFilterAdapterImpl(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        super(originalRequest, ctx);
    }

    //处理所有的request请求
    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
    	
        HttpResponse httpResponse = null;
        try {
        	        	
        	//转化HOST
        	if (httpObject instanceof HttpRequest) {
        		RequestUtil.remove();
        		HttpRequest httpRequest = (HttpRequest) httpObject;
        		replaceHost(httpRequest);
        		httpRequest.headers().set(LogUtil.TRACE_ID, UuidUtil.generateUuid());
        	}
        	
        	//静态资源不拦截
        	if (HostResolverImpl.getSingleton().isStatic(originalRequest.uri())) {
        		return httpResponse;
        	}
        	
        	//进入request过滤器
            ImmutablePair<Boolean, HttpRequestFilter> immutablePair = HttpRequestFilterChain.doFilter(originalRequest, httpObject, ctx);
            
            //过滤不通过的直接进入response过滤器
            if (immutablePair.left) {
                httpResponse = createResponse(HttpResponseStatus.FORBIDDEN, originalRequest, immutablePair.right.getResultDto());
            }
        } catch (Exception e) {
        	
        	//存在异常的直接进入response过滤器
            httpResponse = createResponse(HttpResponseStatus.BAD_GATEWAY, originalRequest, HttpRequestFilterSupport.createResultDto(e));
            logger.error("client's request failed", e);
            
        } 
        
        //返回
        return httpResponse;
    }
    
    @Override
    public HttpResponse proxyToServerRequest(HttpObject httpObject) {
    	//TraceId
    	if (httpObject instanceof HttpRequest) {
    		HttpRequest httpRequest = (HttpRequest) httpObject;
    		String host = httpRequest.headers().get(GatewayConstant.HOST);
        	if (!host.contains(":")) {
        		host = host + ":" + ConfClient.get("server.port");
        	}
        	String oldPort = host.substring(host.indexOf(":") + 1);
    		httpRequest.headers().remove(GatewayConstant.HOST);
    		httpRequest.headers().add(GatewayConstant.HOST, host.replace(oldPort, ConfClient.get("server.port")));
    		httpRequest.headers().remove(GatewayConstant.X_Real_IP);
    	}

        return null;
    }
    
    @Override
    public void proxyToServerResolutionSucceeded(String serverHostAndPort,
                                                 InetSocketAddress resolvedRemoteAddress) {
        if (resolvedRemoteAddress == null) {
            ctx.writeAndFlush(createResponse(HttpResponseStatus.BAD_GATEWAY, originalRequest, HttpRequestFilterSupport.createResultDto(Constant.MESSAGE_GLOBAL_ERROR)));
        } 
    }

    //进入resoponse过滤器
    @Override
    public HttpObject proxyToClientResponse(HttpObject httpObject) {
        if (httpObject instanceof HttpResponse) {
        	HttpResponseFilterChain.doFilter(originalRequest, (HttpResponse) httpObject);
        	if (((HttpResponse) httpObject).status().code() == HttpResponseStatus.BAD_GATEWAY.code()) {
                ctx.writeAndFlush(createResponse(HttpResponseStatus.BAD_GATEWAY, originalRequest, HttpRequestFilterSupport.createResultDto(Constant.MESSAGE_GLOBAL_ERROR)));
        	}
        }
        return httpObject;
    }

    //下游的服务器连接成功
    @Override
    public void proxyToServerConnectionSucceeded(final ChannelHandlerContext serverCtx) {
        ChannelPipeline pipeline = serverCtx.pipeline();
        //当没有修改getMaximumResponseBufferSizeInBytes中buffer默认的大小时,下面两个handler是不存在的
        if (pipeline.get("inflater") != null) {
            pipeline.remove("inflater");
        }
        if (pipeline.get("aggregator") != null) {
            pipeline.remove("aggregator");
        }
        super.proxyToServerConnectionSucceeded(serverCtx);    
    }
    
    //下游服务器连接失败
    @Override
    public void proxyToServerConnectionFailed() {
        try {
   		 	ClientToProxyConnection clientToProxyConnection = (ClientToProxyConnection) ctx.handler();
            Field field = ClientToProxyConnection.class.getDeclaredField("currentServerConnection");
            field.setAccessible(true);
            ProxyToServerConnection proxyToServerConnection = (ProxyToServerConnection) field.get(clientToProxyConnection);
            String remoteIp = proxyToServerConnection.getRemoteAddress().getAddress().getHostAddress();
            int remotePort = proxyToServerConnection.getRemoteAddress().getPort();
            String remoteUrl = remoteIp + ":" + remotePort;
            String serverHostAndPort = proxyToServerConnection.getServerHostAndPort();
            String port = serverHostAndPort.substring(serverHostAndPort.indexOf(":")+1);
            ServerDiscoveryHandlerProvider.getInstance().connectionFail(HostResolverImpl.getSingleton().getServers(port), remoteUrl);
        } catch (Exception e) {
            logger.error("connection of proxy->server is failed", e);
        } 
    }

    //创建resoponse(中途退出错误的场合)
    private HttpResponse createResponse(HttpResponseStatus httpResponseStatus, HttpRequest originalRequest, ResultDto responseDto) {
        HttpHeaders httpHeaders=new DefaultHttpHeaders();
        httpHeaders.add("Transfer-Encoding","chunked");
        HttpResponse httpResponse;
        if (responseDto != null) {
        	ByteBuf buf = io.netty.buffer.Unpooled.copiedBuffer(responseDto.toJSON().toJSONString(), CharsetUtil.UTF_8); 
        	httpResponse  = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus, buf);
        	httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON);
        } else {
            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus);
        }

        //support CORS（服务器跨域请求）
        List<String> originHeader = GatewayConstant.getHeaderValues(originalRequest, "Origin");
        if (originHeader.size() > 0) {
            httpHeaders.set("Access-Control-Allow-Credentials", "true");
            httpHeaders.set("Access-Control-Allow-Origin", originHeader.get(0));
        }
        httpResponse.headers().add(httpHeaders);
        return httpResponse;
    }
    
    //替换host
    public static void replaceHost(HttpRequest httpRequest) {
    	String host = httpRequest.headers().get(GatewayConstant.HOST);
    	if (!host.contains(":")) {
    		host = host + ":" + ConfClient.get("server.port");
    	}
    	String oldPort = host.substring(host.indexOf(":") + 1);
		httpRequest.headers().remove(GatewayConstant.HOST);
		String uri = httpRequest.uri();
		String port = HostResolverImpl.getSingleton().getPort(uri);
		httpRequest.headers().add(GatewayConstant.HOST, host.replace(oldPort, port));
    }
}
