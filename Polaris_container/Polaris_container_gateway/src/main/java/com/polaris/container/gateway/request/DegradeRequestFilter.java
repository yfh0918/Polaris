package com.polaris.container.gateway.request;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.polaris.container.gateway.support.HttpRequestFilterSupport;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.provider.ConfHandlerProviderFactory;
import com.polaris.core.util.PropertyUtil;
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
@Service
public class DegradeRequestFilter extends HttpRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(DegradeRequestFilter.class);
	private final static String FILE_NAME = "degrade.txt";
	private static Set<String> degradeUrlSet = new HashSet<>();
	private static String degradeMessageCode = Constant.RESULT_FAIL;
	private static String degradeMessage = Constant.MESSAGE_GLOBAL_ERROR;

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
    	Set<String> tempDegradeUrlSet = new HashSet<>();
    	String tempDegradeMessageCode = null;
    	String tempDegradeMessage = null;
    	
    	for (String conf : contents) {
    		if (StringUtil.isNotEmpty(conf) && !conf.startsWith("#")) {
    			conf = conf.replace("\n", "");
    			conf = conf.replace("\r", "");

				String[] kv = PropertyUtil.getKeyValue(conf);
				// degrade.url
    			if (kv[0].equals("degrade.url")) {
    				if (StringUtil.isNotEmpty(kv[1])) {
    					tempDegradeUrlSet.add(kv[1]);
    				}
    				
    			}
    			// degrade.message
    			if (kv[0].equals("degrade.message.code")) {
    				if (StringUtil.isNotEmpty(kv[1])) {
    					tempDegradeMessageCode = kv[1];
    				}
    			}
    			// degrade.message
    			if (kv[0].equals("degrade.message")) {
    				if (StringUtil.isNotEmpty(kv[1])) {
    					tempDegradeMessage = kv[1];
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
            
            //降级URL
            HttpRequest httpRequest = (HttpRequest)httpObject;
            String url = CCHttpRequestFilter.getUrl(httpRequest);
            if (degradeUrlSet.size() > 0 && degradeUrlSet.contains(url)) {
            	this.setResultDto(HttpRequestFilterSupport.createResultDto(degradeMessageCode,degradeMessage));
            	return true;
            }
        }
        return false;
    }

}


