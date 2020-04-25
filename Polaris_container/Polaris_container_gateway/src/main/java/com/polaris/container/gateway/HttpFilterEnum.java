package com.polaris.container.gateway;

import com.polaris.container.gateway.pojo.HttpFilterFile;
import com.polaris.container.gateway.pojo.HttpFilterEntity;
import com.polaris.container.gateway.request.ArgsHttpRequestFilter;
import com.polaris.container.gateway.request.CCHttpRequestFilter;
import com.polaris.container.gateway.request.CookieHttpRequestFilter;
import com.polaris.container.gateway.request.CorsRequestFilter;
import com.polaris.container.gateway.request.DegradeRequestFilter;
import com.polaris.container.gateway.request.IpHttpRequestFilter;
import com.polaris.container.gateway.request.PostHttpRequestFilter;
import com.polaris.container.gateway.request.ScannerHttpRequestFilter;
import com.polaris.container.gateway.request.TokenHttpRequestFilter;
import com.polaris.container.gateway.request.UaHttpRequestFilter;
import com.polaris.container.gateway.request.UrlHttpRequestFilter;
import com.polaris.container.gateway.request.WIpHttpRequestFilter;
import com.polaris.container.gateway.request.WUrlHttpRequestFilter;
import com.polaris.container.gateway.response.CorsHttpResponseFilter;
import com.polaris.container.gateway.response.TokenHttpResponseFilter;

public enum HttpFilterEnum {
		
	//默认的requestFilter
	Cors(new HttpFilterEntity(new CorsRequestFilter(), "gateway.cors", 0, new HttpFilterFile("gw_cors.txt"))), 
	Degrade(new HttpFilterEntity(new DegradeRequestFilter(), "gateway.degrade", 2,new HttpFilterFile("gw_degrade.txt"))), 
	WIp(new HttpFilterEntity(new WIpHttpRequestFilter(), "gateway.ip.whitelist", 4,new HttpFilterFile("gw_wip.txt"))), 
	Ip(new HttpFilterEntity(new IpHttpRequestFilter(), "gateway.ip.blacklist", 6,new HttpFilterFile("gw_ip.txt"))), 
	CC(new HttpFilterEntity(new CCHttpRequestFilter(), "gateway.cc", 8,new HttpFilterFile("gw_cc.txt"))),
	Scanner(new HttpFilterEntity(new ScannerHttpRequestFilter(), "gateway.scanner", 10)),
	WUrl(new HttpFilterEntity(new WUrlHttpRequestFilter(), "gateway.url.whitelist", 12,new HttpFilterFile("gw_wurl.txt"))),
	Ua(new HttpFilterEntity(new UaHttpRequestFilter(), "gateway.ua", 14,new HttpFilterFile("gw_ua.txt"))),
	Url(new HttpFilterEntity(new UrlHttpRequestFilter(), "gateway.url.blacklist", 16,new HttpFilterFile("gw_url.txt"))),
	Args(new HttpFilterEntity(new ArgsHttpRequestFilter(), "gateway.args", 18,new HttpFilterFile("gw_args.txt"))),
	Cookie(new HttpFilterEntity(new CookieHttpRequestFilter(), "gateway.cookie", 20,new HttpFilterFile("gw_cookie.txt"))),
	Post(new HttpFilterEntity(new PostHttpRequestFilter(), "gateway.post", 22,new HttpFilterFile("gw_post.txt"),new HttpFilterFile("gw_file.txt"))),
	Token(new HttpFilterEntity(new TokenHttpRequestFilter(), "gateway.token", 24,new HttpFilterFile("gw_token.txt"))),

	//responseFilter
	CorsResponse(new HttpFilterEntity(new CorsHttpResponseFilter(), "gateway.cors", 0)),
	TokenResponse(new HttpFilterEntity(new TokenHttpResponseFilter(), "gateway.token", 2));
	
    // 构造方法  
	private HttpFilterEntity filterEntity;
    private HttpFilterEnum(HttpFilterEntity filterEntity) {  
    	this.filterEntity = filterEntity;
    }
	public HttpFilterEntity getFilterEntity() {
		return this.filterEntity;
	}
	public void setFilterEntity(HttpFilterEntity filterEntity) {
		this.filterEntity = filterEntity;
	}
}
