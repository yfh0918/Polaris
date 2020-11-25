package com.polaris.extension.feign;

import org.apache.http.client.HttpClient;

import com.polaris.core.naming.NamingClient;
import com.polaris.core.util.HttpClientUtil;

import feign.Feign;
import feign.Request.Options;
import feign.Retryer;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

public class FeignClient {
    
    //不启用httpclient的retry
    private static HttpClient defaultHttpClient = HttpClientUtil.createHttpClient(100, 20, 0);

    
    public static <T> T target(Class<T> apiType, String url) {
        return target(apiType,null,null,null,url);
    }
    public static <T> T target(Class<T> apiType, Retryer retryer, String url) {
        return target(apiType,retryer,null,null,url);
    }
    public static <T> T target(Class<T> apiType, Options option, String url) {
        return target(apiType,null,option,null,url);
    }
    public static <T> T target(Class<T> apiType, Retryer retryer,Options option, String url) {
        return target(apiType,retryer,option,null,url);
    }
    
    public static <T> T target(Class<T> apiType, HttpClient httpClient, String url) {
        return target(apiType,null,null,httpClient,url);
    }
    public static <T> T target(Class<T> apiType, Retryer retryer, HttpClient httpClient,String url) {
        return target(apiType,retryer,null,httpClient,url);
    }
    public static <T> T target(Class<T> apiType, Options option, HttpClient httpClient,String url) {
        return target(apiType,null,option,httpClient,url);
    }
    public static <T> T target(Class<T> apiType, Retryer retryer,Options option, HttpClient httpClient, String url) {
        if (retryer == null) {
            retryer = new Retryer.Default();
        }
        if (option == null) {
            option = new Options();
        }
        if (httpClient == null) {
            httpClient = defaultHttpClient;
        }
        return Feign.builder()
                .client(new ApacheHttpClient(httpClient))
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .retryer(retryer)
                .options(option).target(apiType, NamingClient.getRealIpUrl(url));
    }
     
    
}
