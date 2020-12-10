package com.polaris.extension.feign;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.springframework.core.annotation.AnnotationUtils;

import com.polaris.core.Constant;
import com.polaris.core.GlobalContext;
import com.polaris.core.exception.NamingException;
import com.polaris.core.naming.NamingClient;
import com.polaris.core.naming.annotation.NamingRequest;
import com.polaris.core.pojo.ServerHost;
import com.polaris.core.util.HttpClientUtil;
import com.polaris.core.util.StringUtil;

import feign.Feign;
import feign.Request.Options;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

public class FeignClient {
    
    private static HttpClient defaultHttpClient = HttpClientUtil.createHttpClient(100, 20, 0);//不启用CloseableHttpClient的retry
    private static Retryer defaultRetryer = new Retryer.Default(100, SECONDS.toMillis(1), 2);//设置retry
    private static Options defaultOptions = new Options(10, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, true);
    private static Encoder defaultEncoder = new JacksonEncoder();
    private static Decoder defaultDecoder = new JacksonDecoder();
    private static RequestInterceptor[] defaultRequestInterceptors = new RequestInterceptor[]{};
    
    public static void Default(Retryer retryer, Options options, Encoder encoder, Decoder decoder,HttpClient httpClient, RequestInterceptor... requestInterceptors) {
        if (httpClient != null) {
            defaultHttpClient = httpClient;
        }
        if (retryer != null) {
            defaultRetryer = retryer;
        }
        if (options != null) {
            defaultOptions = options;
        }
        if (encoder != null) {
            defaultEncoder = encoder;
        }
        if (decoder != null) {
            defaultDecoder = decoder;
        }
        if (requestInterceptors != null && requestInterceptors.length > 0) {
            defaultRequestInterceptors = requestInterceptors;
        }
    }
    
    public static <T> T target(Class<T> apiType, RequestInterceptor... requestInterceptors) {
        return target(apiType,null,null,null,requestInterceptors);
    }
    public static <T> T target(Class<T> apiType, Retryer retryer, RequestInterceptor... requestInterceptors) {
        return target(apiType,retryer,null,null,requestInterceptors);
    }
    public static <T> T target(Class<T> apiType, Options option, RequestInterceptor... requestInterceptors) {
        return target(apiType,null,option,null,requestInterceptors);
    }
    public static <T> T target(Class<T> apiType, Retryer retryer,Options option, RequestInterceptor... requestInterceptors) {
        return target(apiType,retryer,option,null,requestInterceptors);
    }
    
    public static <T> T target(Class<T> apiType, HttpClient httpClient, RequestInterceptor... requestInterceptors) {
        return target(apiType,null,null,httpClient,requestInterceptors);
    }
    public static <T> T target(Class<T> apiType, Retryer retryer, HttpClient httpClient,RequestInterceptor... requestInterceptors) {
        return target(apiType,retryer,null,httpClient,requestInterceptors);
    }
    public static <T> T target(Class<T> apiType, Options option, HttpClient httpClient,RequestInterceptor... requestInterceptors) {
        return target(apiType,null,option,httpClient,requestInterceptors);
    }
    public static <T> T target(Class<T> apiType, Retryer retryer,Options option,  HttpClient httpClient, RequestInterceptor... requestInterceptors) {
        if (retryer == null) {
            retryer = defaultRetryer;
        }
        if (option == null) {
            option = defaultOptions;
        }
        if (httpClient == null) {
            httpClient = defaultHttpClient;
        }
        if (requestInterceptors == null || requestInterceptors.length == 0) {
            requestInterceptors = defaultRequestInterceptors;
        }
        Encoder encoder = defaultEncoder;
        Decoder decoder = defaultDecoder;
        String url = url(AnnotationUtils.findAnnotation(apiType, NamingRequest.class));
        return target0(apiType,retryer,option,encoder,decoder,httpClient,url,requestInterceptors);
    }
    
    private static <T> T target0(Class<T> apiType, Retryer retryer,Options option,  Encoder encoder, Decoder decoder, HttpClient httpClient, String url,RequestInterceptor[] requestInterceptors) {
        Set<RequestInterceptor> requestInterceptorSet = new HashSet<>();
        requestInterceptorSet.add(new TraceInterceptor());
        for (RequestInterceptor requestInterceptor : requestInterceptors) {
            requestInterceptorSet.add(requestInterceptor);
        }
        return Feign.builder()
                .client(new ApacheHttpClient(httpClient))
                .requestInterceptors(requestInterceptorSet)
                .encoder(encoder)
                .decoder(decoder)
                .retryer(retryer)
                .options(option).target(apiType, url);
    }
    
    public static String url(NamingRequest request) {
        if (request == null) {
            throw new NamingException("NamingRequest is not setted");
        }
        StringBuilder strB = new StringBuilder();
        strB.append(request.protocol());
        if (StringUtil.isNotEmpty(request.group())) {
            strB.append(request.group() + Constant.SERVICE_INFO_SPLITER);
        }
        strB.append(NamingClient.getServer(request.value()));
        if (StringUtil.isNotEmpty(request.context())) {
            if (!request.context().startsWith(ServerHost.SLASH)) {
                strB.append(ServerHost.SLASH);
            }
            strB.append(request.context());
        }
        return strB.toString();
    }
    
    private static class TraceInterceptor implements RequestInterceptor {
        @Override
        public void apply(RequestTemplate requestTemplate) {
            requestTemplate.header(GlobalContext.TRACE_ID, GlobalContext.getTraceId());
            requestTemplate.header(GlobalContext.SPAN_ID, GlobalContext.getSpanId());
        }
    }
    
}
