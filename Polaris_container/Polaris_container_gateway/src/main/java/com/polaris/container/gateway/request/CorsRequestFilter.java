package com.polaris.container.gateway.request;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.provider.ConfHandlerProviderFactory;
import com.polaris.core.util.PropertyUtil;
import com.polaris.core.util.StringUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author:Tom.Yu
 *
 * Description:
 * cc拦截
 */
/**
 * @author:Tom.Yu
 *
 * Description:
 * 跨域请求
 */
@Service
public class CorsRequestFilter extends HttpRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(CorsRequestFilter.class);
	private final static String FILE_NAME = "cors.txt";
	private static Map<String, String> corsMap = new HashMap<>(); 
	private final String DEFAULT_COSR_BODY = "OPTIONS,HEAD,GET,POST";
	private final String CORS_BODY_KEY="Access-Control-Response";

	static {
		
		//先获取
		loadFile(ConfHandlerProviderFactory.get(Type.EXT).get(FILE_NAME));
		
		//后监听
		ConfHandlerProviderFactory.get(Type.EXT).listen(FILE_NAME, new ConfHandlerListener() {
			@Override
			public void receive(String content) {
				loadFile(content);
			}
    	});
    }
    
    private static void loadFile(String content) {
    	if (StringUtil.isEmpty(content)) {
    		logger.error(FILE_NAME + " is null");
    		return;
    	}
    	String[] contents = content.split(Constant.LINE_SEP);
    	Map<String, String> tempCorsMap = new HashMap<>(); 
    	
    	for (String conf : contents) {
    		if (StringUtil.isNotEmpty(conf) && !conf.startsWith("#")) {
    			conf = conf.replace("\n", "");
    			conf = conf.replace("\r", "");
				String[] kv = PropertyUtil.getKeyValue(conf);
				if (StringUtil.isNotEmpty(kv[1])) {
					tempCorsMap.put(kv[0], kv[1]);
				}
    		}
    	}
    	corsMap = tempCorsMap;
    }
    
    public static Map<String, String> getCorsMap() {
    	return corsMap;
    }
    
	@Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            
            //获取request
            HttpRequest httpRequest = (HttpRequest)httpObject;

            //判断是否为OPTION
            if (httpRequest.method() == HttpMethod.OPTIONS) {
            	if (StringUtil.isNotEmpty(corsMap.get(CORS_BODY_KEY))) {
            		this.setResult(corsMap.get(CORS_BODY_KEY));
            	} else {
                	this.setResult(DEFAULT_COSR_BODY);
            	}
            	this.setStatus(HttpResponseStatus.OK);
            	return true;
            }
            
        }
        return false;
    }
	
	

}


