package com.polaris.container.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.provider.ConfHandlerFactory;
import com.polaris.core.config.reader.launcher.ConfLauncherReaderStrategy;
import com.polaris.core.util.StringUtil;

public class HttpFileReader {
	private static Logger logger = LoggerFactory.getLogger(HttpFileReader.class);
	public static HttpFileReader INSTANCE = new HttpFileReader();
	private HttpFileReader() {};
	
    public void readFile(HttpFileListener listener, HttpFile file){
    	
		//先获取
    	String content = ConfHandlerFactory.get(file.getType()).get(file.getGroup(),file.getName());
    	
    	//获取不到-从本地文件系统获取
    	if (StringUtil.isEmpty(content)) {
    		content = ConfLauncherReaderStrategy.INSTANCE.getContents(file.getName());
    	}
    	
    	//load
    	file.setData(content);
    	listener.onChange(file);
		logger.info("file:{} group:{} is added",file.getName(),file.getGroup());
		
		//后监听
		ConfHandlerFactory.get(file.getType()).listen(file.getGroup(),file.getName(), new ConfHandlerListener() {
			@Override
			public void receive(String content) {
		        file.setData(content);
				listener.onChange(file);
				logger.info("file:{} group:{} is updated",file.getName(),file.getGroup());
			}
    	});
    }
}
