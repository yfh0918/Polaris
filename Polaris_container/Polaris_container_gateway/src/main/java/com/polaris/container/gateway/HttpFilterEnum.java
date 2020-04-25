package com.polaris.container.gateway;

import com.polaris.container.gateway.pojo.FileType;
import com.polaris.container.gateway.pojo.FileType.Type;
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
	Cors(new HttpFilterEntity(new CorsRequestFilter(), "gateway.cors", 0, new FileType("cors.txt", Type.KV))), 
	Degrade(new HttpFilterEntity(new DegradeRequestFilter(), "gateway.degrade", 2,new FileType("core.txt", Type.KV))), 
	WIp(new HttpFilterEntity(new WIpHttpRequestFilter(), "gateway.ip.whitelist", 4,new FileType("wip.txt", Type.PATTERN))), 
	Ip(new HttpFilterEntity(new IpHttpRequestFilter(), "gateway.ip.blacklist", 6,new FileType("ip.txt", Type.PATTERN))), 
	CC(new HttpFilterEntity(new CCHttpRequestFilter(), "gateway.cc", 8,new FileType("cc.txt", Type.KV))),
	Scanner(new HttpFilterEntity(new ScannerHttpRequestFilter(), "gateway.scanner", 10)),
	WUrl(new HttpFilterEntity(new WUrlHttpRequestFilter(), "gateway.url.whitelist", 12,new FileType("wurl.txt", Type.PATTERN))),
	Ua(new HttpFilterEntity(new UaHttpRequestFilter(), "gateway.ua", 14,new FileType("ua.txt", Type.PATTERN))),
	Url(new HttpFilterEntity(new UrlHttpRequestFilter(), "gateway.url.blacklist", 16,new FileType("url.txt", Type.PATTERN))),
	Args(new HttpFilterEntity(new ArgsHttpRequestFilter(), "gateway.args", 18,new FileType("args.txt", Type.PATTERN))),
	Cookie(new HttpFilterEntity(new CookieHttpRequestFilter(), "gateway.cookie", 20,new FileType("cookie.txt", Type.PATTERN))),
	Post(new HttpFilterEntity(new PostHttpRequestFilter(), "gateway.post", 22,new FileType("post.txt", Type.PATTERN),new FileType("file.txt", Type.PATTERN))),
	Token(new HttpFilterEntity(new TokenHttpRequestFilter(), "gateway.token", 24,new FileType("token.txt", Type.KV))),

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
