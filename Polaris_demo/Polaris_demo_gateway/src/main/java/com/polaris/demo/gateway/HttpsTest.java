package com.polaris.demo.gateway;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.polaris.core.thread.ThreadPoolBuilder;

public class HttpsTest {
    //private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 4000;
    private static CloseableHttpClient httpClient;
    private static CookieStore cookieStore = new BasicCookieStore();
    static {
//        connMgr = new PoolingHttpClientConnectionManager();
//        connMgr.setMaxTotal(200);
//        connMgr.setDefaultMaxPerRoute(100);

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        configBuilder.setStaleConnectionCheckEnabled(true);
        configBuilder.setCookieSpec(CookieSpecs.STANDARD);
        requestConfig = configBuilder.build();
        refreshHttpClient();
        // initCookieStore(Conf.get("priceInfoCookie"));
        // initCookieStore(Conf.get("accountBalanceCookie"));
        // initCookieStore(Conf.get("refreshCookie"));
    }

    private static void refreshHttpClient() {
        httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setDefaultRequestConfig(requestConfig).setDefaultCookieStore(cookieStore).build();
    }

    public static String post(String url, LinkedHashMap<String, String> lmp,String outputStr) throws Exception {

        HttpPost httppost = new HttpPost(url);
        Header[] headers = { new BasicHeader("Content-Type", "application/x-www-form-urlencoded"),};
        httppost.setHeaders(headers);

        StringEntity uefEntity = new StringEntity(outputStr,"utf-8");//解决中文乱码问题    
        uefEntity.setContentEncoding("UTF-8");    
        uefEntity.setContentType("application/json");   
        httppost.setEntity(uefEntity);
        try {
            HttpResponse response = httpClient.execute(httppost);
            setCookieStore(response);
            System.out.println(EntityUtils.toString(response.getEntity()));
            if (response.getStatusLine().getStatusCode() == 200) {
                String xmlString = EntityUtils.toString(response.getEntity());
                return xmlString;

            }
        } catch (ConnectionPoolTimeoutException e) {
            refreshHttpClient();
            throw e;
        }

        return "";
    }

    public static String get(String url) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            setCookieStore(response);
            if (response.getStatusLine().getStatusCode() == 200) {
                String xmlString = EntityUtils.toString(response.getEntity());
                return xmlString;
            }
        } catch (ConnectionPoolTimeoutException e) {
            refreshHttpClient();
            throw e;
        }

        return "";
    }

    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

                @Override
                public void verify(String host, SSLSocket ssl) throws IOException {
                }

                @Override
                public void verify(String host, X509Certificate cert) throws SSLException {
                }

                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                }
            });
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslsf;
    }

    public static void setCookieStore(HttpResponse httpResponse) {
        Header[] headers = httpResponse.getHeaders("Set-Cookie");
        for (Header header : headers) {
            String cookieValueStr=header.getValue();
            String [] cookieValues=cookieValueStr.split(";");
            String cookieName=cookieValues[0].split("=")[0];
            String cookieValue=cookieValues[0].split("=")[1];
            BasicClientCookie cookie = new BasicClientCookie(cookieName, cookieValue);
            cookie.setDomain("api.gate.io");
            cookie.setPath("/");
            cookieStore.addCookie(cookie);
        }
    }

    public static void initCookieStore(String cookieStr) {
        String cookies[] = cookieStr.split(";");
        for (String acookie : cookies) {
            String[] kv = acookie.split("=");
            BasicClientCookie cookie;
            if (kv.length == 1) {
                cookie = new BasicClientCookie(kv[0].trim(), "");
            } else {
                cookie = new BasicClientCookie(kv[0].trim(), kv[1].trim());
            }
            cookie.setDomain("api.gate.io");
            cookie.setPath("/api2");
            cookieStore.addCookie(cookie);
        }
    }
    public static void main(String[] args) throws Exception {
        System.out.println(get("https://127.0.0.1:8081/#/login"));
    }
    public static void main2(String[] args) throws Exception {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        
        Map<String, Object> chierMap = new HashMap<String, Object>();
        chierMap.put("has_battery", true);
 

        paraMap.put("carrier_code", "60");
        paraMap.put("api_key", "apikey");
        paraMap.put("pickup_info", chierMap);

      //thread pool
        ThreadPoolExecutor threadPool = ThreadPoolBuilder.newBuilder()
                .poolName("NDIHandler Thread Pool")
                .coreThreads(2)
                .maximumThreads(100)
                .keepAliveSeconds(10l)
                .workQueue(new LinkedBlockingDeque<Runnable>(10000))
                .build();
        
        System.out.println(JSON.toJSONString(paraMap));

        String cc = JSON.toJSONString(paraMap);
        for (int i = 0; i < 100000; i++) {
            try {
                threadPool.submit(
                        new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    post("https://127.0.0.1:8081/gateway/cc/ip", null,cc); 
                                } catch (Exception ex) {
                                   
                                }
                                
                            }
                            
                        }
                        
                        );
                
            } catch (Exception ex) {
                
            }
        }

    }
}
