package com.polaris.core.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
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
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
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
import com.polaris.core.naming.ServerDiscoveryHandlerProvider;
 
/**
 * HttpClient工具类
 * 
 * @return
 * @author 
 * @create 
 */
public class HttpClientUtil {
 
	private static final String TRACE_ID = "traceId";
	private static final String RETRY_COUNT = "http.connect.retry.count";
	private static final String POOL_CONN_MAX_COUNT="http.connect.max.conn.count";
	private static final String POOL_CONN_MAX_PERROUTE = "http.connect.max.per.route.count";
	private static final String POOL_CONN_DEFAULT_PERROUTE = "http.connect.default.per.route.count";
	private static final String UTF8 = "UTF-8";
	private static Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);
    static final int TIME_OUT = 10 * 1000;
 
    private static CloseableHttpClient httpClient = null;
 
    private final static Object syncLock = new Object();
 
    private static void config(HttpRequestBase httpRequestBase) {
        // 设置Header等
        // httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
        // httpRequestBase
        // .setHeader("Accept",
        // "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        // httpRequestBase.setHeader("Accept-Language",
        // "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
        // httpRequestBase.setHeader("Accept-Charset",
        // "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");
 
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(TIME_OUT)
                .setConnectTimeout(TIME_OUT).setSocketTimeout(TIME_OUT).build();
        httpRequestBase.setConfig(requestConfig);
    }
 
    /**
     * 获取HttpClient对象
     * 
     * @return
     * @author 
     * @create 
     */
    public static CloseableHttpClient getHttpClient(String url) {
    	
    	//必须为http://获这https://开头
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        int connPoolMaxCount = Integer.parseInt(ConfClient.get(POOL_CONN_MAX_COUNT, "100"));
        int connPoolMaxPerRoute = Integer.parseInt(ConfClient.get(POOL_CONN_MAX_PERROUTE, "50"));
        int connPoolDefaultPerRoute = Integer.parseInt(ConfClient.get(POOL_CONN_DEFAULT_PERROUTE, "20"));
        if (httpClient == null) {
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient = createHttpClient(connPoolMaxCount, connPoolDefaultPerRoute, connPoolMaxPerRoute, hostname, port);
                }
            }
        }
        return httpClient;
    }
 
    /**
     * 创建HttpClient对象
     * 
     * @return
     * @author 
     * @create 
     */
    public static CloseableHttpClient createHttpClient(int maxTotal,
            int maxPerRoute, int maxRoute, String hostname, int port) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory> create().register("http", plainsf)
                .register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);
 
        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                    int executionCount, HttpContext context) {
            	int retryCount = Integer.parseInt(ConfClient.get(RETRY_COUNT, "2"));
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
 
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setRetryHandler(httpRequestRetryHandler).build();
 
        return httpClient;
    }
 
    private static void setPostParams(HttpPost httpost,
            Map<String, Object> params) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, UTF8));
        } catch (UnsupportedEncodingException e) {
        	LOGGER.debug(e.getMessage());
        }
    }
 
    /**
     * GET请求URL获取内容
     * 
     * @param url
     * @return
     * @author 
     * @create 
     */
    public static String post(String orgurl, Map<String, Object> params) {
    	String url = ServerDiscoveryHandlerProvider.getInstance().getUrl(orgurl);
    	LOGGER.debug(url);
        CloseableHttpResponse response = null;
        try {
            HttpPost httppost = new HttpPost(url);
            config(httppost);
            setPostParams(httppost, params);
            trace(httppost);//增加trace编号
            response = getHttpClient(url).execute(httppost,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, UTF8);
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
        	ServerDiscoveryHandlerProvider.getInstance().connectionFail(orgurl, url);
        	LOGGER.debug(e.getMessage());
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
            	LOGGER.debug(e.getMessage());
            }
        }
        return null;
    }
    
    /**
     * GET请求URL获取内容
     * 
     * @param url
     * @return
     * @author 
     * @create 
     */
    public static String post(String orgurl, String body) {
    	String url = ServerDiscoveryHandlerProvider.getInstance().getUrl(orgurl);
    	LOGGER.debug(url);
        CloseableHttpResponse response = null;
        try {
            HttpPost httppost = new HttpPost(url);
            config(httppost);
            trace(httppost);//增加trace编号
            
            //设置body
            StringEntity bodyEntity = new StringEntity(body, Charset.forName(UTF8));
            bodyEntity.setContentEncoding(UTF8);
            bodyEntity.setContentType("application/json");
			httppost.setEntity(bodyEntity);
			
			//返回内容
            response = getHttpClient(url).execute(httppost,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, UTF8);
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
        	ServerDiscoveryHandlerProvider.getInstance().connectionFail(orgurl, url);
        	LOGGER.debug(e.getMessage());
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
            	LOGGER.debug(e.getMessage());
            }
        }
        return null;
    }
 
    /**
     * GET请求URL获取内容
     * 
     * @param url
     * @return
     * @author 
     * @create 
     */
    
    public static String get(String orgurl) {
    	return get(orgurl, null);
    }
    public static String get(String orgurl,  Map<String, Object> params) {
    	String url = ServerDiscoveryHandlerProvider.getInstance().getUrl(orgurl);
    	LOGGER.debug(url);
    	CloseableHttpResponse response = null;
        try {
        	URIBuilder builder = new URIBuilder(url);
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.addParameter(key, String.valueOf(params.get(key)));
                }
            }

            URI uri = builder.build();
            HttpGet httpget = new HttpGet(uri);//增加参数
            config(httpget);
            trace(httpget);
            
            response = getHttpClient(url).execute(httpget,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, UTF8);
            EntityUtils.consume(entity);   //关闭HttpEntity是的流，如果手动关闭了InputStream instream = entity.getContent();这个流，也可以不调用这个方法
            return result;
        } catch (Exception e) {
        	ServerDiscoveryHandlerProvider.getInstance().connectionFail(orgurl, url);
        	LOGGER.debug(e.getMessage());
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
            	LOGGER.debug(e.getMessage());
            }
        }
        return null;
    }
 
    public static void main(String[] args) {
    	
    	//post请求
//    	String ret = HttpClientUtil.post(url, params);
//        jsonRet = new JSONObject(ret);
        
        // URL列表数组
        String[] urisToGet = {
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
 
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
 
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
 
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
 
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
 
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497" };
 
        long start = System.currentTimeMillis();
        try {
            int pagecount = urisToGet.length;
            ExecutorService executors = Executors.newFixedThreadPool(pagecount);
            CountDownLatch countDownLatch = new CountDownLatch(pagecount);
            for (int i = 0; i < pagecount; i++) {
                HttpGet httpget = new HttpGet(urisToGet[i]);
                config(httpget);
                // 启动线程抓取
                executors
                        .execute(new GetRunnable(urisToGet[i], countDownLatch));
            }
            countDownLatch.await();
            executors.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("线程" + Thread.currentThread().getName() + ","
                    + System.currentTimeMillis() + ", 所有线程已完成，开始进入下一步！");
        }
 
        long end = System.currentTimeMillis();
        System.out.println("consume -> " + (end - start));
    }
 
    static class GetRunnable implements Runnable {
        private CountDownLatch countDownLatch;
        private String url;
 
        public GetRunnable(String url, CountDownLatch countDownLatch) {
            this.url = url;
            this.countDownLatch = countDownLatch;
        }
 
        @Override
        public void run() {
            try {
                System.out.println(HttpClientUtil.get(url));
            } finally {
                countDownLatch.countDown();
            }
        }
    }
    
    private static void trace(HttpRequestBase request) {
    	if (StringUtil.isNotEmpty(GlobalContext.getContext(TRACE_ID))) {
        	request.addHeader(TRACE_ID, GlobalContext.getContext(TRACE_ID));
    	}
    }
}
