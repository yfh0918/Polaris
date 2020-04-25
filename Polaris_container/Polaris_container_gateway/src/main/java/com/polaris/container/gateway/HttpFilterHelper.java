package com.polaris.container.gateway;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.polaris.container.gateway.pojo.FileType;
import com.polaris.container.gateway.pojo.HttpFilterEntity;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.provider.ConfHandlerProviderFactory;
import com.polaris.core.util.PropertyUtil;
import com.polaris.core.util.StringUtil;

public abstract class HttpFilterHelper  {
	
	//初始化所有的filter
	public static void init() {
		for (HttpFilterEnum e : HttpFilterEnum.values()) {
			addFilter(e.getFilterEntity());
		}
	}
	
	//外部调用-替换现有的filter
	public synchronized static void replaceFilter(HttpFilterEntity httpFilterEntity, HttpFilter filter) {
		removeFilter(httpFilterEntity);
		httpFilterEntity.setFilter(filter);
		addFilter(httpFilterEntity);
	}
	//外部调用-新增filter
	public synchronized static void addFilter(HttpFilterEntity httpFilterEntity) {
		httpFilterEntity.getFilter().start(httpFilterEntity);
	}
	
	//外部调用-删除filter
	public synchronized static void removeFilter(HttpFilterEntity httpFilterEntity) {
		httpFilterEntity.getFilter().stop(httpFilterEntity);
	}
	
	//用于匹配pattern的Map
	private final static Map<String, Map<String, Pattern>> patternMap = new ConcurrentHashMap<>();

    //匹配
    public static Collection<Pattern> getPattern(HttpFilterEntity httpFilterEntity) {
        return getPattern(httpFilterEntity, 0);
    }
    public static Collection<Pattern> getPattern(HttpFilterEntity httpFilterEntity, int index) {
        return patternMap.get(httpFilterEntity.getFileTypes()[index].getFile()).values();
    }
    public static Pattern getPattern(HttpFilterEntity httpFilterEntity, String key) {
        return getPattern(httpFilterEntity, 0, key);
    }
    public static Pattern getPattern(HttpFilterEntity httpFilterEntity, int index, String key) {
        return patternMap.get(httpFilterEntity.getFileTypes()[index].getFile()).get(key);
    }
    
    //用于匹配keyValue的Map
    private final static Map<String, Map<String, Set<String>>> kvMap = new ConcurrentHashMap<>();
    public static Map<String, Set<String>> getKV(HttpFilterEntity httpFilterEntity) {
    	return getKV(httpFilterEntity, 0);
    }
    public static Map<String, Set<String>> getKV(HttpFilterEntity httpFilterEntity, int index) {
    	return kvMap.get(httpFilterEntity.getFileTypes()[index].getFile());
    }
    
	//创建需要处理的具体文件，比如cc.txt,ip.txt
    public static void create(FileType fileType){
    	
    	//pattern
    	if (fileType.getType() == com.polaris.container.gateway.pojo.FileType.Type.PATTERN) {
    		if (patternMap.containsKey(fileType.getFile())) {
        		return;
        	}
    		
    		//先获取
    		loadPatters(fileType.getFile(), ConfHandlerProviderFactory.get(Type.EXT).get(fileType.getFile()));
    		
    		//后监听
    		ConfHandlerProviderFactory.get(Type.EXT).listen(fileType.getFile(), new ConfHandlerListener() {
    			@Override
    			public void receive(String content) {
    				loadPatters(fileType.getFile(), content);
    			}
        		
        	});
    	}
    	
    	//KV
    	if (fileType.getType() == com.polaris.container.gateway.pojo.FileType.Type.KV) {
    		if (kvMap.containsKey(fileType.getFile())) {
        		return;
        	}
    		//先获取
    		loadKVs(fileType.getFile(), ConfHandlerProviderFactory.get(Type.EXT).get(fileType.getFile()));
    		
    		//后监听
    		ConfHandlerProviderFactory.get(Type.EXT).listen(fileType.getFile(), new ConfHandlerListener() {
    			@Override
    			public void receive(String content) {
    				loadKVs(fileType.getFile(), content);
    			}
        		
        	});
    	}
    	
    }
    private static void loadPatters(String name, String content) {
    	if  (StringUtil.isEmpty(content)) {
        	Map<String, Pattern> contentMap = new ConcurrentHashMap<>();
        	patternMap.put(name, contentMap);
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
    	patternMap.put(name, contentMap);
    }
    private static void loadKVs(String name, String content) {
    	if  (StringUtil.isEmpty(content)) {
        	Map<String, Set<String>> contentMap = new ConcurrentHashMap<>();
        	kvMap.put(name, contentMap);
    		return;
    	}
    	String[] contents = content.split(Constant.LINE_SEP);
    	Map<String, Set<String>> contentMap = new ConcurrentHashMap<>();
    	for (String conf : contents) {
    		if (StringUtil.isNotEmpty(conf)) {
    			conf = conf.replace("\n", "").trim();
    			conf = conf.replace("\r", "").trim();
    			if (!conf.startsWith("#")) {
    				String[] kv = PropertyUtil.getKeyValue(conf);
        			if (kv != null && kv.length == 2) {
        				if (contentMap.containsKey(kv[0])) {
        					contentMap.get(kv[0]).add(kv[1]);
        				} else {
        					Set<String> set = new HashSet<>();
        					set.add(kv[1]);
        					contentMap.put(kv[0], set);
        				}
        			}
    			}
    		}
    	}
    	kvMap.put(name, contentMap);
    }

}
