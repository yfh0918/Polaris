package com.polaris.container.gateway.pojo;

import com.polaris.container.gateway.request.HttpArgsRequestFilter;
import com.polaris.container.gateway.request.HttpCCRequestFilter;
import com.polaris.container.gateway.request.HttpCookieRequestFilter;
import com.polaris.container.gateway.request.HttpCorsRequestFilter;
import com.polaris.container.gateway.request.HttpDegradeRequestFilter;
import com.polaris.container.gateway.request.HttpIpRequestFilter;
import com.polaris.container.gateway.request.HttpPostRequestFilter;
import com.polaris.container.gateway.request.HttpScannerRequestFilter;
import com.polaris.container.gateway.request.HttpTokenRequestFilter;
import com.polaris.container.gateway.request.HttpUaRequestFilter;
import com.polaris.container.gateway.request.HttpUrlRequestFilter;
import com.polaris.container.gateway.request.HttpWIpRequestFilter;
import com.polaris.container.gateway.request.HttpWUrlRequestFilter;
import com.polaris.container.gateway.response.HttpCorsResponseFilter;

public enum HttpFilterEntityEnum {
		
	//默认的requestFilter
	Cors(new HttpFilterEntity(new HttpCorsRequestFilter(), "gateway.cors", 0, new HttpFile("gw_cors.txt"))), 
	Degrade(new HttpFilterEntity(new HttpDegradeRequestFilter(), "gateway.degrade", 2,new HttpFile("gw_degrade.txt"))), 
	WIp(new HttpFilterEntity(new HttpWIpRequestFilter(), "gateway.ip.whitelist", 4,new HttpFile("gw_wip.txt"))), 
	Ip(new HttpFilterEntity(new HttpIpRequestFilter(), "gateway.ip.blacklist", 6,new HttpFile("gw_ip.txt"))), 
	CC(new HttpFilterEntity(new HttpCCRequestFilter(), "gateway.cc", 8,new HttpFile("gw_cc.txt"))),
	Scanner(new HttpFilterEntity(new HttpScannerRequestFilter(), "gateway.scanner", 10)),
	WUrl(new HttpFilterEntity(new HttpWUrlRequestFilter(), "gateway.url.whitelist", 12,new HttpFile("gw_wurl.txt"))),
	Ua(new HttpFilterEntity(new HttpUaRequestFilter(), "gateway.ua", 14,new HttpFile("gw_ua.txt"))),
	Url(new HttpFilterEntity(new HttpUrlRequestFilter(), "gateway.url.blacklist", 16,new HttpFile("gw_url.txt"))),
	Args(new HttpFilterEntity(new HttpArgsRequestFilter(), "gateway.args", 18,new HttpFile("gw_args.txt"))),
	Cookie(new HttpFilterEntity(new HttpCookieRequestFilter(), "gateway.cookie", 20,new HttpFile("gw_cookie.txt"))),
	Post(new HttpFilterEntity(new HttpPostRequestFilter(), "gateway.post", 22,new HttpFile("gw_post.txt"),new HttpFile("gw_file.txt"))),
	Token(new HttpFilterEntity(new HttpTokenRequestFilter(), "gateway.token", 24,new HttpFile("gw_token.txt"))),

	//responseFilter
	CorsResponse(new HttpFilterEntity(new HttpCorsResponseFilter(), "gateway.cors", 0));
	
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
