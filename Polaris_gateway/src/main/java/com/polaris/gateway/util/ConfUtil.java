package com.polaris.gateway.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.polaris.comm.Constant;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.config.ConfListener;
import com.polaris.comm.config.ConfigHandlerProvider;
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
        	ConfClient.addListener(filterType.getFileName(), new ConfListener() {
    			@Override
    			public void receive(String content) {
					loadPatters(filterType.name(), content);
    			}
        		
        	});
        	try {
    			Thread.sleep(100);
    			if (confMap.get(filterType.name()) == null) {
                	String content = ConfigHandlerProvider.getLocalFileContent(filterType.getFileName());
                	loadPatters(filterType.name(), content);
    			}
    		} catch (InterruptedException e) {
    			//nothing
    		}
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
    			Pattern pattern = Pattern.compile(conf);
                patterns.add(pattern);
    		}
    	}
    	confMap.put(name, patterns);
    }
}
