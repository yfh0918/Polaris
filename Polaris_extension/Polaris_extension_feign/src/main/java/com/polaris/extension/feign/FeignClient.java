package com.polaris.extension.feign;

import java.util.HashSet;
import java.util.Set;

import org.apache.http.client.HttpClient;

import com.polaris.core.GlobalContext;
import com.polaris.core.naming.NamingClient;
import com.polaris.core.util.HttpClientUtil;

import feign.Feign;
import feign.Request.Options;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

public class FeignClient {
    
    //不启用CloseableHttpClient的retry
    private static HttpClient defaultHttpClient = HttpClientUtil.createHttpClient(100, 20, 0);

    
    public static <T> T target(Class<T> apiType, String url,RequestInterceptor... requestInterceptors) {
        return target(apiType,null,null,null,url,requestInterceptors);
    }
    public static <T> T target(Class<T> apiType, Retryer retryer, String url,RequestInterceptor... requestInterceptors) {
        return target(apiType,retryer,null,null,url,requestInterceptors);
    }
    public static <T> T target(Class<T> apiType, Options option, String url,RequestInterceptor... requestInterceptors) {
        return target(apiType,null,option,null,url,requestInterceptors);
    }
    public static <T> T target(Class<T> apiType, Retryer retryer,Options option, String url,RequestInterceptor... requestInterceptors) {
        return target(apiType,retryer,option,null,url,requestInterceptors);
    }
    
    public static <T> T target(Class<T> apiType, HttpClient httpClient, String url,RequestInterceptor... requestInterceptors) {
        return target(apiType,null,null,httpClient,url,requestInterceptors);
    }
    public static <T> T target(Class<T> apiType, Retryer retryer, HttpClient httpClient,String url,RequestInterceptor... requestInterceptors) {
        return target(apiType,retryer,null,httpClient,url,requestInterceptors);
    }
    public static <T> T target(Class<T> apiType, Options option, HttpClient httpClient,String url,RequestInterceptor... requestInterceptors) {
        return target(apiType,null,option,httpClient,url,requestInterceptors);
    }
    public static <T> T target(Class<T> apiType, Retryer retryer,Options option,  HttpClient httpClient, String url,RequestInterceptor... requestInterceptors) {
        if (retryer == null) {
            retryer = new Retryer.Default();
        }
        if (option == null) {
            option = new Options();
        }
        if (httpClient == null) {
            httpClient = defaultHttpClient;
        }
        Set<RequestInterceptor> requestInterceptorSet = new HashSet<>();
        requestInterceptorSet.add(new TraceInterceptor());
        if (requestInterceptors != null) {
            for (RequestInterceptor requestInterceptor : requestInterceptors) {
                requestInterceptorSet.add(requestInterceptor);
            }
        }
        return Feign.builder()
                .client(new ApacheHttpClient(httpClient))
                .requestInterceptors(requestInterceptorSet)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .retryer(retryer)
                .options(option).target(apiType, NamingClient.getRealIpUrl(url));
    }
    
    private static class TraceInterceptor implements RequestInterceptor {
        
        @Override
        public void apply(RequestTemplate requestTemplate) {
            requestTemplate.header(GlobalContext.TRACE_ID, GlobalContext.getTraceId());
            requestTemplate.header(GlobalContext.SPAN_ID, GlobalContext.getSpanId());
        }
    }
    
}