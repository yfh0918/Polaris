package com.polaris.sentinel;

import java.util.HashSet;
import java.util.Set;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.polaris.comm.Constant;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.config.ConfListener;
import com.polaris.comm.config.ConfigHandlerProvider;
import com.polaris.comm.util.StringUtil;

public class WebFilterInit {
	private final static String FILE_NAME = "sentinel.txt";
    private volatile static Set<String> staticSet = new HashSet<String>();
    private volatile static Set<String> restUriSet = new HashSet<>();

	private static void loadFile(String content) {
    	String[] contents = content.split(Constant.LINE_SEP);
    	Set<String> TEMP_FILE_TYPE = new HashSet<>();
    	Set<String> TEMP_REST_URI = new HashSet<>();
 
    	for (String conf : contents) {
    		if (StringUtil.isNotEmpty(conf) && !conf.startsWith("#")) {
    			conf = conf.replace("\n", "");
    			conf = conf.replace("\r", "");
				String[] kv = ConfigHandlerProvider.getKeyValue(conf);

    			// 不需要验证token的uri
    			if (kv[0].equals("csp.sentinel.filter.fileType")) {
    				TEMP_FILE_TYPE.add(kv[1]);
    			}

    			// 以xx开头放过的URL
    			if (kv[0].equals("csp.sentinel.filter.restUri")) {
    				TEMP_REST_URI.add(kv[1]);
    			}
    		}
    	}
    	
    	staticSet = TEMP_FILE_TYPE;
    	restUriSet = TEMP_REST_URI;
    }
	
	public void init() throws Exception {
		
		//先获取
		try {
			loadFile(ConfClient.getConfigValue(FILE_NAME));
			
			//后监听
	    	ConfClient.addListener(FILE_NAME, new ConfListener() {
				@Override
				public void receive(String content) {
					loadFile(content);
				}
	    	});
		} catch (Exception ex) {
			//File nothing
		}
   	
		//过滤
		staticSet.add(".css");
		staticSet.add(".js");
		staticSet.add(".jpg");
		staticSet.add(".png");
		staticSet.add(".ico");
		staticSet.add(".gif");
		staticSet.add(".properties");

		//url过滤
		WebCallbackManager.setUrlCleaner(new UrlCleaner() {
			@Override
			public String clean(String originUrl) {
				if (originUrl != null) {
					
					//静态资源,比如css,js等统一规整
					int index = originUrl.lastIndexOf(".");
					if (index > -1) {
						String suffix = originUrl.substring(index).toLowerCase();
						if (staticSet.contains(suffix)) {
							return staticSet.toString();
						}
					}
					
					//rest风格的URI需要统合，比如/user/1,user/2,user/3,etc统合成/user/*
					if (restUriSet.size() > 0) {
						int restindex = originUrl.lastIndexOf("/");
						if (restindex > -1) {
							String rest = originUrl.substring(0, restindex + 1);
							if (restUriSet.contains(rest)) {
								return rest + "*";
							}
						}
					}
				}
				
				return originUrl;
			}
		});
	}
}
