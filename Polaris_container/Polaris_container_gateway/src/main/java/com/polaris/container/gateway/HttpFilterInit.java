package com.polaris.container.gateway;

import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.container.gateway.pojo.HttpFilterEntity;
import com.polaris.container.gateway.request.HttpArgsRequestFilter;
import com.polaris.container.gateway.request.HttpCookieRequestFilter;
import com.polaris.container.gateway.request.HttpIpRequestFilter;
import com.polaris.container.gateway.request.HttpPostRequestFilter;
import com.polaris.container.gateway.request.HttpScannerRequestFilter;
import com.polaris.container.gateway.request.HttpUaRequestFilter;
import com.polaris.container.gateway.request.HttpUrlRequestFilter;
import com.polaris.container.gateway.request.HttpWIpRequestFilter;
import com.polaris.container.gateway.request.HttpWUrlRequestFilter;
import com.polaris.core.config.ConfClient;

public class HttpFilterInit {
    public static void init() {
        init(ConfClient.getAppGroup());
    }
	public static void init(String group) {
	    HttpServerConfigReader.INSTANCE.init(group);
        HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpWIpRequestFilter(), 4,new HttpFile(group,"gw_wip.txt")));
        HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpIpRequestFilter(), 6,new HttpFile(group,"gw_ip.txt")));
        HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpScannerRequestFilter(), 10));
        HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpWUrlRequestFilter(), 12,new HttpFile(group,"gw_wurl.txt")));
        HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpUaRequestFilter(), 14,new HttpFile(group,"gw_ua.txt")));
        HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpUrlRequestFilter(), 16,new HttpFile(group,"gw_url.txt")));
        HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpArgsRequestFilter(), 18,new HttpFile(group,"gw_args.txt")));
        HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpCookieRequestFilter(), 20,new HttpFile(group,"gw_cookie.txt")));
        HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpPostRequestFilter(), 22,new HttpFile(group,"gw_post.txt"),new HttpFile(group,"gw_file.txt")));
	}
}
