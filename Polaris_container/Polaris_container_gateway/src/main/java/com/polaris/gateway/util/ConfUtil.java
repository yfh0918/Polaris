package com.polaris.gateway.util;


import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.config.ConfListener;
import com.polaris.core.util.StringUtil;
import com.polaris.gateway.request.FilterType;

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
    		loadPatters(filterType.name(), ConfClient.getConfigValue(filterType.getFileName()));
    		
    		//后监听
        	ConfClient.addListener(filterType.getFileName(), new ConfListener() {
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
