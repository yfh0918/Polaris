package com.polaris.comm.supports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.polaris.comm.Constant;
import com.polaris.comm.util.LogUtil;

public class RequestWithCookieSupport {

	private static final LogUtil logger = LogUtil.getInstance(RequestWithCookieSupport.class);
	
	private CloseableHttpClient httpClientWithCookieStore = null;

	private RequestWithCookieSupport() {
		CookieStore cookieStore = new BasicCookieStore();
		httpClientWithCookieStore = HttpClientBuilder.create()
				.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
				.setRedirectStrategy(new DefaultRedirectStrategy())
				.setDefaultRequestConfig(getRequestConfig(Constant.CONNECT_MAX_TIME))
				.setDefaultCookieStore(cookieStore)
				.build();
	}
	public static RequestWithCookieSupport instance() {
		return new RequestWithCookieSupport();
	}

	/**
	 * 调用
	 */
	public String sendRequest(String method, String requestURL, Map<String, String> requestParameter) {
		return sendRequest(method, requestURL, requestParameter, null);
	}
	public String sendRequest(String method, String requestURL, Map<String, String> requestParameter, Map<String, String> headerParameter) {
		
		//已经关闭直接返回null
		if (httpClientWithCookieStore == null) {
			return null;
		}
		
		//发布请求
		if (Constant.METHOD_GET.equals(method)) {
			return sendGetRequest(requestURL, requestParameter, headerParameter);
		} else if (Constant.METHOD_POST.equals(method)) {
			return sendPostRequest(requestURL, requestParameter, headerParameter);
		}
		return null;
	}

	/**
	 * Get调用
	 */
	private String sendGetRequest(String requestURL, Map<String, String> requestParameter, Map<String, String> headerParameter) {

		// 请求
		String result = null;

		try {
			// 获取参数
			List<NameValuePair> nvps = getNameValuePairList(requestParameter);
			String str = EntityUtils.toString(new UrlEncodedFormEntity(nvps, Constant.UTF_CODE));

			// 执行http连接
			HttpGet httpGet = new HttpGet(requestURL + "?" + str);
			if (headerParameter != null) {
				for (Map.Entry<String, String> entry : headerParameter.entrySet()) { 
					httpGet.addHeader(entry.getKey().toString(), entry.getValue().toString());
				}  
			}
			httpGet.addHeader(LogUtil.TRACE_ID, Constant.getContext(LogUtil.TRACE_ID));
			logger.debug("param:" + str);
			HttpResponse response = httpClientWithCookieStore.execute(httpGet);

			// 获取结果
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity);
				logger.debug("result" + result);
			}

		} catch (Exception ex) {
			logger.error("sendPostRequest异常", ex);
		}
		return result;
	}

	/**   
	 * @desc  : HttpClient4.x可以自带维持会话功能，只要使用同一个HttpClient且未关闭连接，则可以使用相同会话来访问其他要求登录验证的服务
	 * 			（带Cookie的Post请求）
	 * @author: Yang Hao
	 * @date  : 2017年11月23日 下午4:02:27
	 * @param requestURL
	 * @param requestParameter
	 * @return
	*/
	private String sendPostRequest(String requestURL, Map<String, String> requestParameter, Map<String, String> headerParameter) {
		HttpClientContext context = HttpClientContext.create();
		// 请求
		String result = null;

		try {
			HttpPost httpPost = new HttpPost(requestURL);
			List<NameValuePair> nvps = getNameValuePairList(requestParameter);
			// 获取参数
			UrlEncodedFormEntity urlEntity = new UrlEncodedFormEntity(nvps, Constant.UTF_CODE);
			httpPost.setEntity(urlEntity);
			if (headerParameter != null) {
				for (Map.Entry<String, String> entry : headerParameter.entrySet()) { 
					httpPost.addHeader(entry.getKey().toString(), entry.getValue().toString());
				}  
			}
			httpPost.addHeader(LogUtil.TRACE_ID, Constant.getContext(LogUtil.TRACE_ID));
			logger.debug("param:" + EntityUtils.toString(urlEntity));
			// 发送请求
			CloseableHttpResponse response = httpClientWithCookieStore.execute(httpPost, context);

			// 获取结果
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity);
				logger.debug("result" + result);
			}
		} catch (Exception ex) {
			logger.error("sendPostRequest异常", ex);
		}
		return result;
	}

	private RequestConfig getRequestConfig(int sTimeOut) {
		RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(sTimeOut)
				.setConnectTimeout(sTimeOut).setConnectionRequestTimeout(sTimeOut).build();
		return defaultRequestConfig;
	}

	/**
	 * 获取参数列表
	 */
	private List<NameValuePair> getNameValuePairList(Map<String, String> requestParameter) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (requestParameter != null) {
			for (Map.Entry<String, String> m : requestParameter.entrySet()) {
				nvps.add(new BasicNameValuePair(m.getKey(), m.getValue()));
			}
		}
		return nvps;
	}
	
	/**
	 * 关闭
	 */
	public void close() {
		if (httpClientWithCookieStore != null) {
			try {
				httpClientWithCookieStore.close();
			} catch (IOException e) {
				logger.error(e);
			}
			httpClientWithCookieStore = null;
		}
	}
}
