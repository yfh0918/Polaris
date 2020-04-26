package com.polaris.container.gateway;

import java.util.HashSet;
import java.util.Set;

import com.polaris.container.gateway.pojo.HttpFilterEntity;
import com.polaris.container.gateway.pojo.HttpFilterFile;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfEndPoint;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.provider.ConfHandlerProviderFactory;
import com.polaris.core.config.reader.ConfReaderStrategyDefault;
import com.polaris.core.util.StringUtil;

public class HttpFilterHelper  implements ConfEndPoint{
	
	@Override
	public void init() {
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
    public static void load(HttpFilter filter, HttpFilterFile file){
    	
		//先获取
    	String content = ConfHandlerProviderFactory.get(Type.EXT).get(file.getName());
    	
    	//获取不到-从本地文件系统获取
    	if (StringUtil.isEmpty(content)) {
    		content = ConfReaderStrategyDefault.INSTANCE.getContents(file.getName());
    	}
    	
    	//load
    	load(file, content);
    	filter.onChange(file);
		
		//后监听
		ConfHandlerProviderFactory.get(Type.EXT).listen(file.getName(), new ConfHandlerListener() {
			@Override
			public void receive(String content) {
				load(file, content);
				filter.onChange(file);
			}
    	});
    }
    private static void load(HttpFilterFile file, String content) {
    	Set<String> data = new HashSet<>();
    	if  (StringUtil.isEmpty(content)) {
    		file.setData(data);
    	} else {
        	String[] contents = content.split(Constant.LINE_SEP);
        	for (String conf : contents) {
        		if (StringUtil.isNotEmpty(conf)) {
        			conf = conf.replace("\n", "").trim();
        			conf = conf.replace("\r", "").trim();
        			data.add(conf);
        		}
        	}
        	file.setData(data);
    	}
    }
}
