package com.polaris.container.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.provider.ConfHandlerFactory;
import com.polaris.core.config.reader.launcher.ConfLauncherReaderStrategyFactory;
import com.polaris.core.util.StringUtil;

public class HttpFileReader {
	private static Logger logger = LoggerFactory.getLogger(HttpFileReader.class);
	public static HttpFileReader INSTANCE = new HttpFileReader();
	private HttpFileReader() {};
	
    public void readFile(HttpFileListener listener, HttpFile file){
    	
		//先获取
    	String content = ConfHandlerFactory.getOrCreate(file.getType()).get(file.getName(),file.getType().getGroup());
    	
    	//获取不到-从本地文件系统获取
    	if (StringUtil.isEmpty(content)) {
    		content = ConfLauncherReaderStrategyFactory.get().getContents(file.getName());
    	}
    	
    	//load
    	file.setData(content);
    	listener.onChange(file);
		logger.info("file:{} type:{} is added",file.getName(),file.getType());
		
		//后监听
		ConfHandlerFactory.getOrCreate(file.getType()).listen(file.getName(),file.getType().getGroup(), new ConfHandlerListener() {
			@Override
			public void receive(String content) {
		        file.setData(content);
				listener.onChange(file);
				logger.info("file:{} type:{} is updated",file.getName(),file.getType());
			}
    	});
    }
}
