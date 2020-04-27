package com.polaris.container.gateway;

import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.pojo.HttpFilterEntity;
import com.polaris.container.gateway.pojo.HttpFilterFile;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.provider.ConfHandlerProviderFactory;
import com.polaris.core.config.reader.ConfReaderStrategyDefault;
import com.polaris.core.util.StringUtil;

public class HttpFilterHelper {
	private static Logger logger = LoggerFactory.getLogger(HttpFilterHelper.class);
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
		logger.info("add filter:{}",httpFilterEntity.getFilter().getClass().getSimpleName());
		httpFilterEntity.getFilter().start();
	}
	
	//外部调用-删除filter
	public void removeFilter(HttpFilterEntity httpFilterEntity) {
		logger.info("remove filter:{}",httpFilterEntity.getFilter().getClass().getSimpleName());
		httpFilterEntity.getFilter().stop();
	}
	
	//创建需要处理的具体文件，比如cc.txt,ip.txt
    public void loadFile(HttpFilterCallback callback, HttpFilterFile file){
    	
		//先获取
    	String content = ConfHandlerProviderFactory.get(Type.EXT).get(file.getName());
    	
    	//获取不到-从本地文件系统获取
    	if (StringUtil.isEmpty(content)) {
    		content = ConfReaderStrategyDefault.INSTANCE.getContents(file.getName());
    	}
    	
    	//load
    	load(file, content);
    	callback.onChange(file);
		logger.info("file:{} type:{} is added",file.getName(),Type.EXT);
		
		//后监听
		ConfHandlerProviderFactory.get(Type.EXT).listen(file.getName(), new ConfHandlerListener() {
			@Override
			public void receive(String content) {
				load(file, content);
				callback.onChange(file);
				logger.info("file:{} type:{} is updated",file.getName(),Type.EXT);
			}
    	});
    }
    private void load(HttpFilterFile file, String content) {
    	Set<String> data = new LinkedHashSet<>();
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
