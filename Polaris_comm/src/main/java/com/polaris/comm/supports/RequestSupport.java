package com.polaris.comm.supports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.polaris.comm.Constant;
import com.polaris.comm.util.LogUtil;

public class RequestSupport {

	private static final LogUtil logger = LogUtil.getInstance(RequestSupport.class);
	
	private RequestSupport() {
	}

	/**
	 * 调用
	 */
	public static String sendRequest(String method, String requestURL, Map<String, String> requestParameter) {
		return sendRequest(method, requestURL, requestParameter, null);
	}
	public static String sendRequest(String method, String requestURL, Map<String, String> requestParameter, Map<String, String> headerParameter) {
		if (Constant.METHOD_GET.equals(method)) {
			return sendGetRequest(requestURL, requestParameter, headerParameter);
		} else if (Constant.METHOD_POST.equals(method)) {
			return sendPostRequest(requestURL, requestParameter, headerParameter);
		}
		return null;
	}

	/**
	 * Post调用
	 */
	private static String sendPostRequest(String requestURL, Map<String, String> requestParameter, Map<String, String> headerParameter) {

		// 请求
		String result = null;

		try {
			// 获取参数
			List<NameValuePair> nvps = getNameValuePairList(requestParameter);
			UrlEncodedFormEntity urlEntity = new UrlEncodedFormEntity(nvps, Constant.UTF_CODE);

			// 执行http连接
			HttpClient httpclient = HttpClients.custom()
					.setDefaultRequestConfig(getRequestConfig(Constant.CONNECT_MAX_TIME)).build();
			HttpPost httpPost = new HttpPost(requestURL);
			httpPost.setEntity(urlEntity);
			if (headerParameter != null) {
				for (Map.Entry<String, String> entry : headerParameter.entrySet()) { 
					httpPost.addHeader(entry.getKey().toString(), entry.getValue().toString());
				}  
			}
			httpPost.addHeader(LogUtil.TRACE_ID, Constant.getContext(LogUtil.TRACE_ID));
			
			logger.debug("param:" + EntityUtils.toString(urlEntity));
			HttpResponse response = httpclient.execute(httpPost);

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
	 * Get调用
	 */
	private static String sendGetRequest(String requestURL, Map<String, String> requestParameter, Map<String, String> headerParameter) {

		// 请求
		String result = null;

		try {
			// 获取参数
			List<NameValuePair> nvps = getNameValuePairList(requestParameter);
			String str = EntityUtils.toString(new UrlEncodedFormEntity(nvps, Constant.UTF_CODE));

			// 执行http连接
			HttpClient httpclient = HttpClients.custom()
					.setDefaultRequestConfig(getRequestConfig(Constant.CONNECT_MAX_TIME)).build();
			HttpGet httpGet = new HttpGet(requestURL + "?" + str);
			if (headerParameter != null) {
				for (Map.Entry<String, String> entry : headerParameter.entrySet()) { 
					httpGet.addHeader(entry.getKey().toString(), entry.getValue().toString());
				}  
			}
			httpGet.addHeader(LogUtil.TRACE_ID, Constant.getContext(LogUtil.TRACE_ID));

			logger.debug("param:" + str);
			HttpResponse response = httpclient.execute(httpGet);

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

	private static RequestConfig getRequestConfig(int sTimeOut) {
		RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(sTimeOut)
				.setConnectTimeout(sTimeOut).setConnectionRequestTimeout(sTimeOut).build();
		return defaultRequestConfig;
	}

	/**
	 * 获取参数列表
	 */
	private static List<NameValuePair> getNameValuePairList(Map<String, String> requestParameter) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (requestParameter != null) {
			for (Map.Entry<String, String> m : requestParameter.entrySet()) {
				nvps.add(new BasicNameValuePair(m.getKey(), m.getValue()));
			}
		}
		return nvps;
	}
	
	public static void main( String[] args ) throws Exception {
		for (int i = 0; i < 10000; i++) {
			String result = RequestSupport.sendRequest(Constant.METHOD_POST, "http://127.0.0.1:9005/api/demo/getUserInfo", null);
			Thread.sleep(10);
			System.out.println(result);
		}
	}
}
