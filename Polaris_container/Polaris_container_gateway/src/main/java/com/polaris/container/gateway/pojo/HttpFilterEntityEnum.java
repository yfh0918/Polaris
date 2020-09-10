package com.polaris.container.gateway.pojo;

import com.polaris.container.gateway.request.HttpArgsRequestFilter;
import com.polaris.container.gateway.request.HttpCookieRequestFilter;
import com.polaris.container.gateway.request.HttpIpRequestFilter;
import com.polaris.container.gateway.request.HttpScannerRequestFilter;
import com.polaris.container.gateway.request.HttpUaRequestFilter;
import com.polaris.container.gateway.request.HttpUrlRequestFilter;
import com.polaris.container.gateway.request.HttpWIpRequestFilter;
import com.polaris.container.gateway.request.HttpWUrlRequestFilter;

public enum HttpFilterEntityEnum {
		
	//默认的requestFilter
	WIp(new HttpFilterEntity(new HttpWIpRequestFilter(), 4,new HttpFile("gw_wip.txt"))), 
	Ip(new HttpFilterEntity(new HttpIpRequestFilter(), 6,new HttpFile("gw_ip.txt"))), 
	Scanner(new HttpFilterEntity(new HttpScannerRequestFilter(), 10)),
	WUrl(new HttpFilterEntity(new HttpWUrlRequestFilter(), 12,new HttpFile("gw_wurl.txt"))),
	Ua(new HttpFilterEntity(new HttpUaRequestFilter(), 14,new HttpFile("gw_ua.txt"))),
	Url(new HttpFilterEntity(new HttpUrlRequestFilter(), 16,new HttpFile("gw_url.txt"))),
	Args(new HttpFilterEntity(new HttpArgsRequestFilter(), 18,new HttpFile("gw_args.txt"))),
	Cookie(new HttpFilterEntity(new HttpCookieRequestFilter(), 20,new HttpFile("gw_cookie.txt")));
//	Post(new HttpFilterEntity(new HttpPostRequestFilter(), 22,new HttpFile("gw_post.txt"),new HttpFile("gw_file.txt")));

    // 构造方法  
	private HttpFilterEntity filterEntity;
    private HttpFilterEntityEnum(HttpFilterEntity filterEntity) {  
    	this.filterEntity = filterEntity;
    }
	public HttpFilterEntity getFilterEntity() {
		return this.filterEntity;
	}
	public void setFilterEntity(HttpFilterEntity filterEntity) {
		this.filterEntity = filterEntity;
	}
}
