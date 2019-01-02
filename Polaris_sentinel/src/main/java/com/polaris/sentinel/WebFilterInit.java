package com.polaris.sentinel;

import java.util.HashSet;
import java.util.Set;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.StringUtil;

public class WebFilterInit {
	public void init() throws Exception {
		//过滤
		Set<String> staticSet = new HashSet<>();
		staticSet.add(".css");
		staticSet.add(".js");
		staticSet.add(".jpg");
		staticSet.add(".png");
		staticSet.add(".ico");
		staticSet.add(".gif");
		staticSet.add(".properties");
		String fileTypes = ConfClient.get("csp.sentinel.filter.fileType", false);
		if (StringUtil.isNotEmpty(fileTypes)) {
			String[] types = fileTypes.split("\\|");
			for (String type : types) {
				staticSet.add(type);
			}
		}
		String staticString = staticSet.toString();
		
		//rest风格的URI需要统合，比如/user/1,user/2,user/3,etc统合成/user/*
		Set<String> restUriSet = new HashSet<>();
		String restUri = ConfClient.get("csp.sentinel.filter.restUri", false);
		if (StringUtil.isNotEmpty(restUri)) {
			String[] uris = restUri.split("\\|");
			for (String uri : uris) {
				restUriSet.add(uri);
			}
		}

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
							return staticString;
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
