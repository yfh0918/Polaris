package com.polaris.container.gateway;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.provider.ConfHandlerProviderFactory;
import com.polaris.core.util.PropertyUtil;
import com.polaris.core.util.StringUtil;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpStatic {
	
    // 监听的静态页面地址
    private static final String LISTEN = "static.txt";
    private static Map<String, Map<String, String>> urlMap = new HashMap<>();

    static {
    	
    	//先获取
    	loadUpstream(ConfHandlerProviderFactory.get(Type.EXT).get(LISTEN));
    	
    	//后监听
    	ConfHandlerProviderFactory.get(Type.EXT).listen(LISTEN, new ConfHandlerListener() {
            @Override
            public void receive(String content) {
                loadUpstream(content);
            }
        });
        
    }
    
    //载入需要代理的IP(需要动态代理)
    private static void loadUpstream(String content) {
        if (StringUtil.isEmpty(content)) {
        	urlMap = new HashMap<>();
            return;
        }

        Map<String, Map<String, String>> tempUriMap = new HashMap<>();
        String[] contents = content.split(Constant.LINE_SEP);
        for (String detail : contents) {
        	detail = detail.replace("\n", "");
        	detail = detail.replace("\r", "");
            String[] keyvalue = PropertyUtil.getKeyValue(detail);
            Map<String, String> contentMap = new HashMap<>();
            if (keyvalue != null) {
            	String[] parameters = keyvalue[1].split(";");

        		//第一个locaction
            	contentMap.put("location", parameters[0]);
            	
            	//第二个
            	if (parameters.length > 1) {
            		String[] parameterKV = PropertyUtil.getKeyValue(parameters[1]);
            		contentMap.put(parameterKV[0], parameterKV[1]);
            	}
            	
            	//第三个
            	if (parameters.length > 2) {
            		String[] parameterKV = PropertyUtil.getKeyValue(parameters[2]);
            		contentMap.put(parameterKV[0], parameterKV[1]);
            	}
            	
            	//加入map
            	tempUriMap.put(keyvalue[0], contentMap);
            }
        }
        
        urlMap = tempUriMap;
    }
    
    public static boolean isStatic(HttpRequest request) {
    	// 获取URI
        String uri = request.uri();

        //设置uri
        if (!uri.substring(1).contains("/")) {
    		uri = uri + "/";
    	}
        
        //判断url
        for (Map.Entry<String, Map<String, String>> entry : urlMap.entrySet()) { 
        	if (uri.startsWith(entry.getKey() +"/")) {
        		return true;
        	}
        }
    	return false;
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HttpResponse showStatic(HttpRequest request,ChannelHandlerContext ctx) throws Exception {
		
        // 设置不支持favicon.ico文件
        String uri = request.uri();
        if ("favicon.ico".equals(uri)) {
            return null;
        }

        // 获取URI
        if (!uri.substring(1).contains("/")) {
    		uri = uri + "/";
    	}
        
        // 状态为1xx的话，继续请求
        if (HttpUtil.is100ContinueExpected(request)) {
            return send100Continue(ctx);
        }

        //判断url
        Map<String, String> urls = null;
        String context = null;
        for (Map.Entry<String, Map<String, String>> entry : urlMap.entrySet()) { 
        	if (uri.startsWith(entry.getKey() +"/")) {
        		context = entry.getKey();
        		urls = entry.getValue();
        		break;
        	}
        }
        if (context == null) {
        	return null;
        }
        
        //找到位置
        if ((context + "/").equals(uri)) {
        	uri = context + "/" + urls.get("startup");
        } 
        
        //不包含相应的context
        if (!uri.startsWith(context + "/")) {
        	uri = context + "/" + urls.get("error");//直接返回error
        }
        
        //去掉context后获取真实的物理路径
        String path = urls.get("location") + uri.substring(context.length());
        File html = new File(path);

        //定义response
        HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);

        // 当文件不存在的时候，将资源指向NOT_FOUND
        if (!html.exists()) {
            html = new File(urls.get("location") + "/" + urls.get("error"));
            response.setStatus(HttpResponseStatus.NOT_FOUND);
        }

        RandomAccessFile file = new RandomAccessFile(html, "r");

        // 设置文件格式内容
        String lowPath = path.toLowerCase();
        if (lowPath.endsWith(".html")){
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        }else if(lowPath.endsWith(".js")){
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/x-javascript");
        }else if(lowPath.endsWith(".css")){
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/css; charset=UTF-8");
        }

        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if (keepAlive) {
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.write(response);
        ChannelFuture lastContentFuture;
        ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
        lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);  
        lastContentFuture.addListener(new GenericFutureListener() {
			@Override
            public void operationComplete(Future future) {
            	try {
					file.close();
				} catch (IOException e) {
				}
            }
        });

        // 写入文件尾部
        if (!keepAlive) {
        	lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
        return response;
    }

    private static HttpResponse send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
        return response;
    }
    
}
