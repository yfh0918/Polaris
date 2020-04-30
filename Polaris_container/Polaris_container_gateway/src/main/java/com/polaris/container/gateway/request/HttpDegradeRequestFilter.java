package com.polaris.container.gateway.request;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.pojo.HttpFilterFile;
import com.polaris.core.Constant;
import com.polaris.core.pojo.KeyValuePair;
import com.polaris.core.util.PropertyUtil;
import com.polaris.core.util.ResultUtil;
import com.polaris.core.util.StringUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

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
 * 降级，针对URL
 */
public class HttpDegradeRequestFilter extends HttpRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(HttpDegradeRequestFilter.class);
	private static Set<String> degradeUrlSet = new HashSet<>();
	private static String degradeMessageCode = Constant.RESULT_FAIL;
	private static String degradeMessage = Constant.MESSAGE_GLOBAL_ERROR;

	@Override
	public void onChange(HttpFilterFile file) {
    	Set<String> tempDegradeUrlSet = new HashSet<>();
    	String tempDegradeMessageCode = null;
    	String tempDegradeMessage = null;
    	for (String conf : file.getData()) {
    		KeyValuePair kv = PropertyUtil.getKeyValue(conf);
			if (kv != null) {
				// degrade.url
    			if (kv.getKey().equals("degrade.url")) {
    				if (StringUtil.isNotEmpty(kv.getValue())) {
    					tempDegradeUrlSet.add(kv.getValue());
    				}
    				
    			}
    			// degrade.message
    			if (kv.getKey().equals("degrade.message.code")) {
    				if (StringUtil.isNotEmpty(kv.getValue())) {
    					tempDegradeMessageCode = kv.getValue();
    				}
    			}
    			// degrade.message
    			if (kv.getKey().equals("degrade.message")) {
    				if (StringUtil.isNotEmpty(kv.getValue())) {
    					tempDegradeMessage = kv.getValue();
    				}
    			}
			}
    	}
    	if (tempDegradeMessageCode != null) {
        	degradeMessageCode = tempDegradeMessageCode;
    	}
    	if (tempDegradeMessage != null) {
        	degradeMessage = tempDegradeMessage;
    	}
    	degradeUrlSet = tempDegradeUrlSet;
    }
    
	@Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            
            //获取request
            HttpRequest httpRequest = (HttpRequest)httpObject;

            //降级URL
            String url = HttpCCRequestFilter.getUrl(httpRequest);
            if (degradeUrlSet.size() > 0 && degradeUrlSet.contains(url)) {
            	this.setResult(ResultUtil.create(degradeMessageCode,degradeMessage).toJSONString());
            	return true;
            }
        }
        return false;
    }

}


