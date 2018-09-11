package com.polaris.gateway.util;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.polaris.gateway.request.FilterType;
import com.polaris.comm.util.LogUtil;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class ConfUtil {
	private static LogUtil logger = LogUtil.getInstance(ConfUtil.class);
    
    private final static Map<String, List<Pattern>> confMap = new ConcurrentHashMap<>();

    static {
    	//第一次加载
    	watchFilterType(true);
    }

    public static List<Pattern> getPattern(String type) {
        return confMap.get(type);
    }
    
    public static void watchFilterType(boolean isAlways) {
    	for (FilterType filterType : FilterType.values()) {
    		if (isAlways || filterType.isModified()) {
                try (InputStream inputStream = ConfUtil.class.getClassLoader().getResourceAsStream(filterType.getFileName())) {
                    List<?> confs = IOUtils.readLines(inputStream);
                    List<Pattern> patterns = new ArrayList<>();
                    for (Object conf : confs) {
                        Pattern pattern = Pattern.compile((String) conf);
                        patterns.add(pattern);
                    }
                    confMap.put(filterType.name(), patterns);
                } catch (IOException e) {
                    logger.error("{}配置文件加载失败", filterType.name(),e);
                }

    		}
        }
    }
}
