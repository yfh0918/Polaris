package com.polaris.gateway.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.polaris.comm.Constant;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.config.ConfListener;
import com.polaris.comm.util.StringUtil;
import com.polaris.gateway.request.FilterType;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class ConfUtil {
    private final static Map<String, List<Pattern>> confMap = new ConcurrentHashMap<>();

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

    public static List<Pattern> getPattern(String type) {
        return confMap.get(type);
    }
    
    private static void loadPatters(String name, String content) {
    	if  (StringUtil.isEmpty(content)) {
    		confMap.put(name, new ArrayList<>());
    		return;
    	}
    	String[] contents = content.split(Constant.LINE_SEP);
    	List<Pattern> patterns = new ArrayList<>();
    	for (String conf : contents) {
    		if (StringUtil.isNotEmpty(conf)) {
    			conf = conf.replace("\n", "");
    			conf = conf.replace("\r", "");
    			Pattern pattern = Pattern.compile(conf);
                patterns.add(pattern);
    		}
    	}
    	confMap.put(name, patterns);
    }
}
