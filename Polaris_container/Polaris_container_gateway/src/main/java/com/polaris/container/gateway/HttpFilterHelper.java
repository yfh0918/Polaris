package com.polaris.container.gateway;

import java.util.HashSet;
import java.util.Set;

import com.polaris.container.gateway.pojo.HttpFilterEntity;
import com.polaris.container.gateway.pojo.HttpFilterFile;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.provider.ConfHandlerProviderFactory;
import com.polaris.core.config.reader.ConfReaderStrategyDefault;
import com.polaris.core.util.StringUtil;

public class HttpFilterHelper {
	public static HttpFilterHelper INSTANCE = new HttpFilterHelper();
	private HttpFilterHelper() {};
	
	//外部调用-替换现有的filter
	public void replaceFilter(HttpFilterEntity httpFilterEntity, HttpFilter filter) {
		removeFilter(httpFilterEntity);
		httpFilterEntity.setFilter(filter);
		addFilter(httpFilterEntity);
	}
	//外部调用-新增filter
	public void addFilter(HttpFilterEntity httpFilterEntity) {
		httpFilterEntity.getFilter().start();
	}
	
	//外部调用-删除filter
	public void removeFilter(HttpFilterEntity httpFilterEntity) {
		httpFilterEntity.getFilter().stop();
	}
	
	//创建需要处理的具体文件，比如cc.txt,ip.txt
    public void load(HttpFilterEvent httpFilterEvent, HttpFilterFile file){
    	
		//先获取
    	String content = ConfHandlerProviderFactory.get(Type.EXT).get(file.getName());
    	
    	//获取不到-从本地文件系统获取
    	if (StringUtil.isEmpty(content)) {
    		content = ConfReaderStrategyDefault.INSTANCE.getContents(file.getName());
    	}
    	
    	//load
    	load(file, content);
    	httpFilterEvent.onChange(file);
		
		//后监听
		ConfHandlerProviderFactory.get(Type.EXT).listen(file.getName(), new ConfHandlerListener() {
			@Override
			public void receive(String content) {
				load(file, content);
				httpFilterEvent.onChange(file);
			}
    	});
    }
    private void load(HttpFilterFile file, String content) {
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
