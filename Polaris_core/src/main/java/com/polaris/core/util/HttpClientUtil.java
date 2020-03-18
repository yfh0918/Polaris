package com.polaris.core.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.GlobalContext;
import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.provider.ServerStrategyProvider;
 
/**
 * HttpClient工具类
 * 
 * @return
 * @author 
 * @create 
 */
public class HttpClientUtil {
 

	private static final String HTTP = "http";
	private static final String HTTPS = "https";
	
	private static final String RETRY_COUNT = "http.connect.retryCount";
	private static final String POOL_CONN_MAX_COUNT="http.connect.maxCount";
	private static final String POOL_CONN_PERROUTE = "http.connect.perRouteCount";
	private static final String REQUEST_TIME_OUT = "http.request.timeout";
	private static final String UTF8 = "UTF-8";
	private static int timeout = Integer.parseInt(ConfClient.get(REQUEST_TIME_OUT, "10000"));
	private static Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);
 
    private static CloseableHttpClient defaultHttpClient = null;
 
    private final static Object syncLock = new Object();
 
    private static void setTimeout(HttpRequestBase httpRequestBase, int timeout) {
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeout)
                .setConnectTimeout(timeout).setSocketTimeout(timeout).build();
        httpRequestBase.setConfig(requestConfig);
    }
 
    /**
     * 获取HttpClient对象
     * 
     * @return
     * @author 
     * @create 
     */
    private static CloseableHttpClient getHttpClient(CloseableHttpClient... httpClient) {
    	
    	//用户自定义优先
    	if (httpClient != null && httpClient.length > 0) {
    		return httpClient[0];
    	}
    	
    	//没有自定义可以选着默认
        if (defaultHttpClient == null) {
            synchronized (syncLock) {
                if (defaultHttpClient == null) {
                    int connPoolMaxCount = Integer.parseInt(ConfClient.get(POOL_CONN_MAX_COUNT, "100"));
                    int connPoolDefaultPerRoute = Integer.parseInt(ConfClient.get(POOL_CONN_PERROUTE, "20"));
                	int retryCount = Integer.parseInt(ConfClient.get(RETRY_COUNT, "2"));
                	defaultHttpClient = createHttpClient(connPoolMaxCount, connPoolDefaultPerRoute, retryCount);
                }
            }
        }
        return defaultHttpClient;
    }
 
    /**
     * 创建HttpClient对象
     * 
     * @return
     * @author 
     * @create 
     */
    public static CloseableHttpClient createHttpClient(int maxTotal, int maxPerRoute,int retryCount) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory> create().register(HTTP, plainsf)
                .register(HTTPS, sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
 
        // 请求重试处理
        CloseableHttpClient httpClient = null;
        if (retryCount > 0) {
            HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
                public boolean retryRequest(IOException exception,
                        int executionCount, HttpContext context) {
                    if (executionCount >= retryCount) {// 如果已经重试了5次，就放弃
                        return false;
                    }
                    if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                        return true;
                    }
                    if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                        return false;
                    }
                    if (exception instanceof InterruptedIOException) {// 超时
                        return false;
                    }
                    if (exception instanceof UnknownHostException) {// 目标服务器不可达
                        return false;
                    }
                    if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                        return false;
                    }
                    if (exception instanceof SSLException) {// SSL握手异常
                        return false;
                    }
     
                    HttpClientContext clientContext = HttpClientContext
                            .adapt(context);
                    HttpRequest request = clientContext.getRequest();
                    // 如果请求是幂等的，就再次尝试
                    if (!(request instanceof HttpEntityEnclosingRequest)) {
                        return true;
                    }
                    return false;
                }
            };
            httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .setRetryHandler(httpRequestRetryHandler).build();
        } else {
            httpClient = HttpClients.custom()
                    .setConnectionManager(cm).build();
        }
        return httpClient;
    }
 

 
    /**
     * request请求URL获取内容
     * 
     * @param orgurl
     * @return
     * @author 
     * @create 
     */
    public static String request(HTTPRequestParameter parameter, CloseableHttpClient... httpClient) {
    	HttpUriRequest request = null;
        try {
        	
        	//构建request
        	request = getHttpUriRequest(parameter);
        	
        	//结构返回
        	return getHttpUriResponse(getHttpClient(httpClient),request);
        	
        } catch (Exception ex) {
        	setHttpException(parameter.getUrl(),request.getURI().toString(),ex);
        } 
        return null;
    }
    
    /**
     * post请求URL获取内容
     * 
     * @param orgurl
     * @return
     * @author 
     * @create 
     */
    public static String post(String orgurl, CloseableHttpClient... httpClient) {
    	return post(orgurl,timeout, httpClient);
    }
    public static String post(String orgurl, int timeout, CloseableHttpClient... httpClient) {
    	return post(orgurl,timeout, new HashMap<>(),httpClient);
    }

    public static String post(String orgurl, Map<String, Object> requestParams, CloseableHttpClient... httpClient) {
    	return post(orgurl,timeout,requestParams,httpClient);
    }

    public static String post(String orgurl, int timeout, Map<String, Object> requestParams, CloseableHttpClient... httpClient) {
    	return post(orgurl,timeout,requestParams,null,httpClient);
    }
    
    public static String post(String orgurl, Map<String, Object> requestParams, Map<String, String> headParams, CloseableHttpClient... httpClient) {
    	return post(orgurl, timeout, requestParams, headParams, httpClient);
    }
    public static String post(String orgurl, int timeout, Map<String, Object> requestParams, Map<String, String> headParams, CloseableHttpClient... httpClient) {
    	HTTPRequestParameter parameter = new HTTPRequestParameter();
    	parameter.setRequstType(RequstType.HTTP_POST);
    	parameter.setUrl(orgurl);
    	parameter.setTimeout(timeout);
    	parameter.setRequestParams(requestParams);
    	parameter.setHeadParams(headParams);
    	return request(parameter,httpClient);
    }
    
    /**
     * post-body请求URL获取内容
     * 
     * @param orgurl
     * @return
     * @author 
     * @create 
     */
    public static String post(String orgurl, String body, CloseableHttpClient... httpClient) {
    	return post(orgurl,body,timeout,httpClient);
    }
    public static String post(String orgurl, String body, int timeout, CloseableHttpClient... httpClient) {
    	return post(orgurl,body,timeout, null,httpClient);
    }
    public static String post(String orgurl, String body, Map<String, String> headParams, CloseableHttpClient... httpClient) {
    	return post(orgurl, body, timeout, headParams, httpClient);
    }
    public static String post(String orgurl, String body, int timeout, Map<String, String> headParams, CloseableHttpClient... httpClient) {
    	HTTPRequestParameter parameter = new HTTPRequestParameter();
    	parameter.setRequstType(RequstType.HTTP_POST_BODY);
    	parameter.setUrl(orgurl);
    	parameter.setBody(body);
    	parameter.setHeadParams(headParams);
    	parameter.setTimeout(timeout);
    	return request(parameter, httpClient);
    }

    
    /**
     * post-multipart请求URL获取内容
     * 
     * @param orgurl
     * @return
     * @author 
     * @create 
     */
    public static String postFileMultiPart(String orgurl,Map<String,ContentBody> requestParam, CloseableHttpClient... httpClient) {
    	return postFileMultiPart(orgurl, timeout, requestParam,httpClient);
    }
    public static String postFileMultiPart(String orgurl,int timeout, Map<String,ContentBody> requestParam, CloseableHttpClient... httpClient) {
    	return postFileMultiPart(orgurl, timeout, requestParam, null,httpClient);
    }
    public static String postFileMultiPart(String orgurl,Map<String,ContentBody> requestContentBodys, Map<String, String> headParams, CloseableHttpClient... httpClient) {
    	return postFileMultiPart(orgurl,timeout,requestContentBodys,headParams,httpClient);
    }
    public static String postFileMultiPart(String orgurl,int timeout, Map<String,ContentBody> requestContentBodys, Map<String, String> headParams, CloseableHttpClient... httpClient) {
    	HTTPRequestParameter parameter = new HTTPRequestParameter();
    	parameter.setRequstType(RequstType.HTTP_POST_MULTIPART);
    	parameter.setUrl(orgurl);
    	parameter.setRequestContentBodys(requestContentBodys);
    	parameter.setHeadParams(headParams);
    	parameter.setTimeout(timeout);
    	return request(parameter,httpClient);
    }

 
    /**
     * GET请求URL获取内容
     * 
     * @param url
     * @return
     * @author 
     * @create 
     */
    public static String get(String orgurl, CloseableHttpClient... httpClient) {
    	return get(orgurl, timeout,httpClient);
    }
    public static String get(String orgurl, int timeout, CloseableHttpClient... httpClient) {
    	return get(orgurl, timeout, null,httpClient);
    }
    public static String get(String orgurl,  Map<String, Object> params, CloseableHttpClient... httpClient) {
    	return get(orgurl, timeout, params, httpClient);
    }
    public static String get(String orgurl, int timeout, Map<String, Object> params, CloseableHttpClient... httpClient) {
    	return get(orgurl, timeout, params, null,httpClient);
    }
    public static String get(String orgurl,  Map<String, Object> requestParams, Map<String, String> headParams, CloseableHttpClient... httpClient) {
    	return get(orgurl, timeout,requestParams,headParams,httpClient);
    }
    public static String get(String orgurl,  int timeout, Map<String, Object> requestParams, Map<String, String> headParams, CloseableHttpClient... httpClient) {
    	///构建request
    	HTTPRequestParameter parameter = new HTTPRequestParameter();
    	parameter.setRequstType(RequstType.HTTP_GET);
    	parameter.setUrl(orgurl);
    	parameter.setRequestParams(requestParams);
    	parameter.setHeadParams(headParams);
    	parameter.setTimeout(timeout);
    	return request(parameter,httpClient);
    }
    
    //获取uri
    private static HttpUriRequest getHttpUriRequest( 
    		HTTPRequestParameter parameter) throws Exception {
    	String url = ServerStrategyProvider.INSTANCE.getUrl(parameter.getUrl());
    	LOGGER.info(url);
    	HttpRequestBase request = null;

    	//GET
    	if (parameter.getRequstType() == RequstType.HTTP_GET) {
    		URIBuilder builder = new URIBuilder(url);
            if (parameter.getRequestParams() != null) {
                for(Entry<String,Object> param : parameter.getRequestParams().entrySet()){
                	builder.addParameter(param.getKey(), param.getValue().toString());
                }

            }
            URI uri = builder.build();
            request = new HttpGet(uri);
    	} else {
    		URI uri = URI.create(url);
    		HttpPost httpost = new HttpPost(uri);
    		if (parameter.getRequstType() == RequstType.HTTP_POST){
    			//POST
        		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        		if (parameter.getRequestParams() != null) {
                    for(Entry<String,Object> param : parameter.getRequestParams().entrySet()){
                    	nvps.add(new BasicNameValuePair(param.getKey(), param.getValue().toString()));
                    }
        		}
                httpost.setEntity(new UrlEncodedFormEntity(nvps, UTF8));
    		} else if (parameter.getRequstType() == RequstType.HTTP_POST_BODY) {
        		//设置body
                StringEntity bodyEntity = new StringEntity(parameter.getBody(), Charset.forName(UTF8));
                bodyEntity.setContentEncoding(UTF8);
                bodyEntity.setContentType("application/json");
                httpost.setEntity(bodyEntity);
        	} else if (parameter.getRequstType() == RequstType.HTTP_POST_MULTIPART){
        		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                for(Entry<String,ContentBody> param : parameter.getRequestContentBodys().entrySet()){
                	multipartEntityBuilder.addPart(param.getKey(), param.getValue());
                }
                HttpEntity reqEntity = multipartEntityBuilder.build();
                httpost.setEntity(reqEntity);
        	}
            request = httpost;
    	}
    	
    	//设置其他参数
    	setTimeout(request,parameter.getTimeout());
        setHeaderParams(request,parameter.getHeadParams());
        return request;
    }
    
    //执行结果
    private static String getHttpUriResponse(CloseableHttpClient httpClient,HttpUriRequest request) throws Exception {
    	try (CloseableHttpResponse response = getHttpClient(httpClient).execute(request,HttpClientContext.create())) {
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, UTF8);
            EntityUtils.consume(entity);   //关闭HttpEntity是的流，如果手动关闭了InputStream instream = entity.getContent();这个流，也可以不调用这个方法
            return result;
    	}
    }
    
    //错误处理
    private static void setHttpException(String orgurl, String url, Exception ex) {
    	ServerStrategyProvider.INSTANCE.connectionFail(orgurl, url);
    	if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug("ERROR:",ex);
    	}
    }
 
    //设置头部
    private static void setHeaderParams(HttpRequestBase request, Map<String, String> headParams) {
    	if (StringUtil.isNotEmpty(GlobalContext.getTraceId())) {
        	request.addHeader(GlobalContext.TRACE_ID, GlobalContext.getTraceId());
    	}
    	request.addHeader(GlobalContext.PARENT_ID, GlobalContext.getModuleId());//传递下去
        if (headParams != null) {
        	for (Map.Entry<String, String> entry : headParams.entrySet()) {
        		request.addHeader(entry.getKey(), entry.getValue());
        	}
        }
    }

    public static void main(String[] args) {
    	System.out.println(HttpClientUtil.get("http://blog.csdn.net/catoop/article/details/38849497"));
    	System.out.println(HttpClientUtil.post("http://blog.csdn.net/catoop/article/details/38849497"));
    }
    
    public static class HTTPRequestParameter {
    	private RequstType requestType;
    	private String url;
    	private String body;
    	private Map<String, Object> requestParams = new HashMap<>();
    	private Map<String, String> headParams = new HashMap<>();
    	private Map<String,ContentBody> requestContentBodys = new HashMap<>();
    	private int timeout;
    	public RequstType getRequstType() {
			return requestType;
		}
		public void setRequstType(RequstType requestType) {
			this.requestType = requestType;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
		public Map<String, Object> getRequestParams() {
			return requestParams;
		}
		public void setRequestParam(String key, Object value) {
			this.requestParams.put(key, value);
		}
		public void setRequestParams(Map<String, Object> requestParams) {
			this.requestParams = requestParams;
		}
		public Map<String, String> getHeadParams() {
			return headParams;
		}
		public void setHeadParam(String key, String value) {
			this.headParams.put(key, value);
		}
		public void setHeadParams(Map<String, String> headParams) {
			this.headParams = headParams;
		}
		public Map<String, ContentBody> getRequestContentBodys() {
			return requestContentBodys;
		}
		public void setRequestContentBody(String key, ContentBody value) {
			this.requestContentBodys.put(key, value);
		}
		public void setRequestContentBodys(Map<String, ContentBody> requestContentBodys) {
			this.requestContentBodys = requestContentBodys;
		}
		public int getTimeout() {
			return timeout;
		}
		public void setTimeout(int timeout) {
			this.timeout = timeout;
		}
    }
    
    public static enum RequstType {
    	HTTP_GET("get"),
    	HTTP_POST("post"),
    	HTTP_POST_BODY("body"),
    	HTTP_POST_MULTIPART("multipart");
        private String type;
        RequstType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
    
}
