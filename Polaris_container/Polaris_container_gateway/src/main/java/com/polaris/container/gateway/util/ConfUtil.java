package com.polaris.container.gateway.util;


import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.polaris.container.gateway.request.FilterType;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.provider.ConfHandlerProvider;
import com.polaris.core.util.StringUtil;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class ConfUtil {
    private final static Map<String, Map<String, Pattern>> confMap = new ConcurrentHashMap<>();

    static {
    	//第一次加载
    	for (FilterType filterType : FilterType.values()) {
    		
    		//先获取
    		loadPatters(filterType.name(), ConfHandlerProvider.INSTANCE.get(filterType.getFileName()));
    		
    		//后监听
    		ConfHandlerProvider.INSTANCE.listen(filterType.getFileName(), new ConfHandlerListener() {
    			@Override
    			public void receive(String content) {
					loadPatters(filterType.name(), content);
    			}
        		
        	});
    	}
    }

    public static Collection<Pattern> getPattern(String type) {
        return confMap.get(type).values();
    }
    
    public static Pattern getPattern(String type, String key) {
        return confMap.get(type).get(key);
    }
    
    private static void loadPatters(String name, String content) {
    	if  (StringUtil.isEmpty(content)) {
        	Map<String, Pattern> contentMap = new ConcurrentHashMap<>();
    		confMap.put(name, contentMap);
    		return;
    	}
    	String[] contents = content.split(Constant.LINE_SEP);
    	Map<String, Pattern> contentMap = new ConcurrentHashMap<>();
    	for (String conf : contents) {
    		if (StringUtil.isNotEmpty(conf)) {
    			conf = conf.replace("\n", "").trim();
    			conf = conf.replace("\r", "").trim();
    			Pattern pattern = Pattern.compile(conf);
    			contentMap.put(conf, pattern);
    		}
    	}
    	confMap.put(name, contentMap);
    }
}
