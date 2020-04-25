package com.polaris.container.gateway;

import java.util.HashSet;
import java.util.Set;

import com.polaris.container.gateway.pojo.FileType;
import com.polaris.container.gateway.pojo.HttpFilterEntity;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.provider.ConfHandlerProviderFactory;
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
		httpFilterEntity.getFilter().start();
	}
	
	//外部调用-删除filter
	public synchronized static void removeFilter(HttpFilterEntity httpFilterEntity) {
		httpFilterEntity.getFilter().stop();
	}
	
	//创建需要处理的具体文件，比如cc.txt,ip.txt
    public static void create(HttpFilter filter, FileType fileType){
    	
		//先获取
    	load(fileType, ConfHandlerProviderFactory.get(Type.EXT).get(fileType.getFile()));
    	filter.onChange(fileType);
		
		//后监听
		ConfHandlerProviderFactory.get(Type.EXT).listen(fileType.getFile(), new ConfHandlerListener() {
			@Override
			public void receive(String content) {
				load(fileType, content);
				filter.onChange(fileType);
			}
    	});
    }
    private static void load(FileType fileType, String content) {
    	Set<String> data = new HashSet<>();
    	if  (StringUtil.isEmpty(content)) {
    		fileType.setData(data);
    	} else {
        	String[] contents = content.split(Constant.LINE_SEP);
        	for (String conf : contents) {
        		if (StringUtil.isNotEmpty(conf)) {
        			conf = conf.replace("\n", "").trim();
        			conf = conf.replace("\r", "").trim();
        			data.add(conf);
        		}
        	}
        	fileType.setData(data);
    	}
    }
}
