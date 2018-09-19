package com.polaris.comm.supports;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.comm.Constant;
import com.polaris.comm.util.StringUtil;
import com.polaris.comm.util.WeightedRoundRobinScheduling;
import com.polaris.comm.util.WeightedRoundRobinScheduling.Server;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class HttpClientSupport {

    private static Logger LOGGER = LoggerFactory.getLogger(HttpClientSupport.class);
    private static final String TRACE_ID = "traceId";
    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";

	private static Cache cache;
	static {
		String cacheName = HttpClientSupport.class.getName();
		CacheConfiguration cacheConf = new CacheConfiguration(cacheName,200);//创建一个叫pedCache的缓存
		cacheConf.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU);
		cache = new Cache(cacheConf);
		CacheManager cacheManager= CacheManager.create();
		cacheManager.addCache(cache);
	}

    public static String doGet(String url, Map<String, Object> param) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            url = loadbalance(url);
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, String.valueOf(param.get(key)));
                }
            }

            URI uri = builder.build();
            HttpGet httpGet = new HttpGet(uri);
            trace(httpGet);
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            LOGGER.error("doGet 发起网络请求异常：{}", e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                LOGGER.error("doGet 发起网络请求,response close 异常：{}", e.getMessage());
            }
        }
        return StringUtils.EMPTY;
    }

    public static String doGet(String url) {
        return doGet(url, null);
    }

    public static String doPost(String url, Map<String, Object> param) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            url = loadbalance(url);
            HttpPost httpPost = new HttpPost(url);
            trace(httpPost);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, String.valueOf(param.get(key))));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, Charset.forName("UTF-8"));
                httpPost.setEntity(entity);              
            }
            response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            LOGGER.error("doPost 发起网络请求异常：{}", e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                LOGGER.error("doPost 发起网络请求,response close 异常：{}", e.getMessage());
            }
        }

        return StringUtils.EMPTY;
    }

    public static String doPostCookie(String url, Map<String, Object> param, CookieStore cookie) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setDefaultCookieStore(cookie);
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        CloseableHttpResponse response = null;
        try {
            url = loadbalance(url);
            HttpPost httpPost = new HttpPost(url);
            trace(httpPost);
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, String.valueOf(param.get(key))));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                httpPost.setEntity(entity);
            }
            response = closeableHttpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            LOGGER.error("doPostCookie 发起网络请求异常：{}", e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                closeableHttpClient.close();
            } catch (IOException e) {
                LOGGER.error("doPostCookie 发起网络请求,response close 异常：{}", e.getMessage());
            }
        }

        return StringUtils.EMPTY;
    }

    public static String doPost(String url) {
        return doPost(url, null);
    }

    public static String doPostJson(String url, String json, String lang) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            url = loadbalance(url);
            if (StringUtil.isNotEmpty(lang)) {
            	url = url + "?lang=" + lang;
            }
            HttpPost httpPost = new HttpPost(url);
            trace(httpPost);
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            LOGGER.error("doPostJson 发起网络请求异常：{},url={},json={}", e.getMessage(),url,json);
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                LOGGER.error("doPostJson 发起网络请求,response close 异常：{}", e.getMessage());
            }
        }

        return StringUtils.EMPTY;
    }

    public static void httpAsync(String url, Map<String, Object> paramMap) {
        url = loadbalance(url);
        String paramUrl = url;
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000)
                .build();
            CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig)
                .build();
            httpclient.start();
            StringBuffer params = new StringBuffer();
            if (null != paramMap) {
                Iterator<String> it = paramMap.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next().trim();
                    if (null != paramMap.get(key) && StringUtils.isNotEmpty(String.valueOf(paramMap.get(key)))) {
                        params.append(key + "=" + paramMap.get(key).toString().replaceAll(" ", ""));
                    } else {
                        params.append(key + "=" + paramMap.get(key));
                    }
                    if (it.hasNext()) {
                        params.append("&");
                    }
                }
                paramUrl = url + "?" + params.toString();
            }
            final HttpGet request = new HttpGet(paramUrl);
            request.setHeader("Connection", "close");
            trace(request);
            Future<HttpResponse> execute = httpclient.execute(request, null);
            HttpResponse httpResponse = execute.get();
            LOGGER.info("{}", httpResponse);
            httpclient.close();
        } catch (Exception e) {
            LOGGER.error("httpAsync 发起网络请求异常：{}", e.getMessage());
        }
    }

    public static void httpAsyncJson(String url, String json, String lang) {
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(300000).setConnectTimeout(300000)
                .build();
            CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig)
                .build();
            httpclient.start();
            url = loadbalance(url);
            if (StringUtil.isNotEmpty(lang)) {
            	url = url + "?lang=" + lang;
            }
            final HttpPost request = new HttpPost(url);
            request.setHeader("Content-type", "application/json; charset=utf-8");
            request.setHeader("Connection", "Close");
            String sessionId = getSessionId();
            request.setHeader("SessionId", sessionId);
            trace(request);
            StringEntity entity = new StringEntity(json, Charset.forName("UTF-8"));
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            request.setEntity(entity);
            Future<HttpResponse> execute = httpclient.execute(request, null);
            HttpResponse httpResponse = execute.get();
            LOGGER.debug("{}", httpResponse);
            httpclient.close();
        } catch (Exception e) {
            LOGGER.error("httpAsyncJson 发起网络请求异常：{}", e.getMessage());
        }
    }

    public static void httpAsync(String url) {
        httpAsync(url, null);
    }

    private static String getSessionId() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        return str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23)
            + str.substring(24);
    }

    private static void trace(HttpRequestBase request) {
    	if (StringUtil.isNotEmpty(Constant.getContext(TRACE_ID))) {
        	request.addHeader(TRACE_ID, Constant.getContext(TRACE_ID));
    	}
    }
    
    private static String loadbalance(String url) {
    	String returnUrl;
    	Element urlObject = cache.get(url);
    	if (urlObject == null) {
    		synchronized(url.intern()){
    			urlObject = cache.get(url);
    			if (urlObject == null) {
    				String[] serversInfo = url.split(",");
    				List<WeightedRoundRobinScheduling.Server> serverList = new ArrayList<>();
    				for (String serverInfo : serversInfo) {
    					boolean httpPrefix = false;
    					boolean httpsPrefix = false;
    					if (serverInfo.toLowerCase().startsWith(HTTP_PREFIX)) {
    						httpPrefix = true;
    						serverInfo = serverInfo.substring(HTTP_PREFIX.length());
    					}
    					if (serverInfo.toLowerCase().startsWith(HTTPS_PREFIX)) {
    						httpsPrefix = true;
    						serverInfo = serverInfo.substring(HTTPS_PREFIX.length());
    					}
    		            String[] si = serverInfo.split(":");
    		            if (si.length == 2) {
    		            	if (httpPrefix) {
    		            		si[0] = HTTP_PREFIX + si[0];
    		            	}
    		            	if (httpsPrefix) {
    		            		si[0] = HTTPS_PREFIX + si[0];
    		            	}
    		                WeightedRoundRobinScheduling.Server server = new WeightedRoundRobinScheduling.Server(si[0], Integer.valueOf(si[1]), 1);
    		                serverList.add(server);
    		            } else if (si.length == 3) {
    		            	if (httpPrefix) {
    		            		si[0] = HTTP_PREFIX + si[0];
    		            	}
    		            	if (httpsPrefix) {
    		            		si[0] = HTTPS_PREFIX + si[0];
    		            	}
    		                WeightedRoundRobinScheduling.Server server = new WeightedRoundRobinScheduling.Server(si[0], Integer.valueOf(si[1]), Integer.valueOf(si[2]));
    		                serverList.add(server);
    		            }
    		        }
    				Element element = new Element(url, new WeightedRoundRobinScheduling(serverList));
    				element.setTimeToIdle(0);
    				element.setTimeToLive(0);
    				cache.put(element);
    				urlObject = cache.get(url);
    			}
    		}
    	}
    	Server server = ((WeightedRoundRobinScheduling)urlObject.getObjectValue()).getServer();
    	returnUrl = server.getIp() + ":" + server.getPort();
    	return returnUrl;
    }

    public static void main(String[] args) {
    	loadbalance("https://localhost:8080");
    }
}

