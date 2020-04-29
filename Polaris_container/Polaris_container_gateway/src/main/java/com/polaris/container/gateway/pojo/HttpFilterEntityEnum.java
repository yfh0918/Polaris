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
import com.polaris.container.gateway.response.HttpTokenResponseFilter;

public enum HttpFilterEntityEnum {
		
	//默认的requestFilter
	Cors(new HttpFilterEntity(new HttpCorsRequestFilter(), "gateway.cors", 0, new HttpFilterFile("gw_cors.txt"))), 
	Degrade(new HttpFilterEntity(new HttpDegradeRequestFilter(), "gateway.degrade", 2,new HttpFilterFile("gw_degrade.txt"))), 
	WIp(new HttpFilterEntity(new HttpWIpRequestFilter(), "gateway.ip.whitelist", 4,new HttpFilterFile("gw_wip.txt"))), 
	Ip(new HttpFilterEntity(new HttpIpRequestFilter(), "gateway.ip.blacklist", 6,new HttpFilterFile("gw_ip.txt"))), 
	CC(new HttpFilterEntity(new HttpCCRequestFilter(), "gateway.cc", 8,new HttpFilterFile("gw_cc.txt"))),
	Scanner(new HttpFilterEntity(new HttpScannerRequestFilter(), "gateway.scanner", 10)),
	WUrl(new HttpFilterEntity(new HttpWUrlRequestFilter(), "gateway.url.whitelist", 12,new HttpFilterFile("gw_wurl.txt"))),
	Ua(new HttpFilterEntity(new HttpUaRequestFilter(), "gateway.ua", 14,new HttpFilterFile("gw_ua.txt"))),
	Url(new HttpFilterEntity(new HttpUrlRequestFilter(), "gateway.url.blacklist", 16,new HttpFilterFile("gw_url.txt"))),
	Args(new HttpFilterEntity(new HttpArgsRequestFilter(), "gateway.args", 18,new HttpFilterFile("gw_args.txt"))),
	Cookie(new HttpFilterEntity(new HttpCookieRequestFilter(), "gateway.cookie", 20,new HttpFilterFile("gw_cookie.txt"))),
	Post(new HttpFilterEntity(new HttpPostRequestFilter(), "gateway.post", 22,new HttpFilterFile("gw_post.txt"),new HttpFilterFile("gw_file.txt"))),
	Token(new HttpFilterEntity(new HttpTokenRequestFilter(), "gateway.token", 24,new HttpFilterFile("gw_token.txt"))),

	//responseFilter
	CorsResponse(new HttpFilterEntity(new HttpCorsResponseFilter(), "gateway.cors", 0)),
	TokenResponse(new HttpFilterEntity(new HttpTokenResponseFilter(), "gateway.token", 2));
	
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
