package com.polaris.container.gateway.pojo;

import com.google.common.net.HostAndPort;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

@SuppressWarnings("deprecation")
public class HttpRequestWrapper implements HttpRequest {

    protected HttpRequest httpRequest;
    
    private String context;

    private HostAndPort hostAndPort;
    
    private String serverHostAndPort;
    
    private HttpProxy httpProxy;
    
    private String orgUri;
    
    public HttpRequestWrapper(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }
    
    public HttpRequest getOrgHttpRequest() {
        return this.httpRequest;
    }
    
    @Override
    public HttpVersion getProtocolVersion() {
        return httpRequest.getProtocolVersion();
    }

    @Override
    public HttpVersion protocolVersion() {
        return httpRequest.protocolVersion();
    }

    @Override
    public HttpHeaders headers() {
        return httpRequest.headers();
    }

    @Override
    public DecoderResult getDecoderResult() {
        return httpRequest.getDecoderResult();
    }

    @Override
    public DecoderResult decoderResult() {
        return httpRequest.decoderResult();
    }

    @Override
    public void setDecoderResult(DecoderResult result) {
        httpRequest.setDecoderResult(result);
    }

    @Override
    public HttpMethod getMethod() {
        return httpRequest.getMethod();
    }

    @Override
    public HttpMethod method() {
        return httpRequest.method();
    }

    @Override
    public HttpRequest setMethod(HttpMethod method) {
        return httpRequest.setMethod(method);
    }

    @Override
    public String getUri() {
        return httpRequest.getUri();
    }

    @Override
    public String uri() {
        return httpRequest.uri();
    }

    @Override
    public HttpRequest setUri(String uri) {
        return httpRequest.setUri(uri);
    }

    @Override
    public HttpRequest setProtocolVersion(HttpVersion version) {
        return httpRequest.setProtocolVersion(version);
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public HostAndPort getHostAndPort() {
        return hostAndPort;
    }

    public void setHostAndPort(HostAndPort hostAndPort) {
        this.hostAndPort = hostAndPort;
    }

    public String getServerHostAndPort() {
        return serverHostAndPort;
    }

    public void setServerHostAndPort(String serverHostAndPort) {
        this.serverHostAndPort = serverHostAndPort;
    }

    public HttpProxy getHttpProxy() {
        return httpProxy;
    }

    public void setHttpProxy(HttpProxy httpProxy) {
        this.httpProxy = httpProxy;
    }

    public String getOrgUri() {
        return orgUri;
    }

    public void setOrgUri(String orgUri) {
        this.orgUri = orgUri;
    }

}
