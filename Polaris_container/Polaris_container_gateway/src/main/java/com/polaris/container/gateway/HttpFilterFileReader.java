package com.polaris.container.gateway;

import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.pojo.HttpFilterFile;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.provider.ConfHandlerProviderFactory;
import com.polaris.core.config.reader.ConfReaderStrategyDefault;
import com.polaris.core.util.StringUtil;

public class HttpFilterFileReader {
	private static Logger logger = LoggerFactory.getLogger(HttpFilterFileReader.class);
	public static HttpFilterFileReader INSTANCE = new HttpFilterFileReader();
	private HttpFilterFileReader() {};
	
    public void readFile(HttpFilterCallback callback, HttpFilterFile file){
    	
		//先获取
    	String content = ConfHandlerProviderFactory.get(file.getType()).get(file.getName());
    	
    	//获取不到-从本地文件系统获取
    	if (StringUtil.isEmpty(content)) {
    		content = ConfReaderStrategyDefault.INSTANCE.getContents(file.getName());
    	}
    	
    	//load
    	loadFile(file, content);
    	callback.onChange(file);
		logger.info("file:{} type:{} is added",file.getName(),file.getType());
		
		//后监听
		ConfHandlerProviderFactory.get(file.getType()).listen(file.getName(), new ConfHandlerListener() {
			@Override
			public void receive(String content) {
				loadFile(file, content);
				callback.onChange(file);
				logger.info("file:{} type:{} is updated",file.getName(),file.getType());
			}
    	});
    }
    private void loadFile(HttpFilterFile file, String content) {
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
