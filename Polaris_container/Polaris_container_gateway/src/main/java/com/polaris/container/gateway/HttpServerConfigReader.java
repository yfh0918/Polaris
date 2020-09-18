package com.polaris.container.gateway;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.polaris.container.gateway.pojo.HttpCors;
import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.container.gateway.pojo.HttpHtml;
import com.polaris.container.gateway.pojo.HttpProtocol;
import com.polaris.container.gateway.pojo.HttpProxy;
import com.polaris.core.config.ConfEndPoint;
import com.polaris.core.util.JacksonUtil;
import com.polaris.core.util.StringUtil;

import io.netty.handler.codec.http.HttpMethod;

public class HttpServerConfigReader implements HttpFileListener , ConfEndPoint{
    public static final String NAME = "gw_server.json";
    
    @Override
    public void init() {
        HttpFileReader.INSTANCE.readFile(this, new HttpFile(NAME));
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void onChange(HttpFile file) {
        String serverJson = file.getData();
        Map<String, Object> dataMap = JacksonUtil.toObj(serverJson, Map.class);
        if (dataMap == null) {
            return;
        }
        
        //create protocol object
        createHttpProtocolObject(dataMap);
        
        //create html object
        createHttpHtmlObject(dataMap);
        
        //create cors object
        createHttpCorsObject(dataMap);
        
        //create proxy object
        createHttpProxyObject(dataMap);
    }
    
    @SuppressWarnings("unchecked")
    private void createHttpProtocolObject(Map<String, Object> dataMap) {
        if (dataMap.containsKey("protocol")) {
            Map<String, Map<String, String>> protocolMap = (Map<String, Map<String, String>>)dataMap.get("protocol");
            for (Map.Entry<String, Map<String, String>> elementName : protocolMap.entrySet()) {
                String name = elementName.getKey();
                if (name.equals("connection")) {
                    HttpProtocol.setConnectionMap(elementName.getValue());
                } else if (name.equals("tls")) {
                    HttpProtocol.setTlsMap(elementName.getValue());
                } else if (name.equals("http11")) {
                    HttpProtocol.setHttp11Map(elementName.getValue());
                } else if (name.equals("http20")) {
                    HttpProtocol.setHttp20Map(elementName.getValue());
                } else if (name.equals("websocket")) {
                    HttpProtocol.setWebsocketMap(elementName.getValue());
                }
            }
        }
        
    }
    
    @SuppressWarnings("unchecked")
    private void createHttpCorsObject(Map<String, Object> dataMap) {
        if (dataMap.containsKey("cors")) {
            Map<String, Object> coreMap = (Map<String, Object>)dataMap.get("cors");
            if (coreMap.containsKey("enable")) {
                HttpCors.setEnable(Boolean.parseBoolean(coreMap.get("enable").toString()));
            }
            if (coreMap.containsKey("allowCredentials")) {
                HttpCors.setAllowCredentials(Boolean.parseBoolean(coreMap.get("allowCredentials").toString()));
            }
            if (coreMap.containsKey("maxAge")) {
                HttpCors.setMaxAge(Long.parseLong(coreMap.get("maxAge").toString()));
            }
            if (coreMap.containsKey("allowOrigin")) {
                List<Map<String,String>> parameterList = (List<Map<String,String>>)coreMap.get("allowOrigin");
                List<String> resultList = new ArrayList<>();
                createCorsElement(parameterList, resultList);
                HttpCors.setAllowOrigin(resultList);
            }
            if (coreMap.containsKey("allowHeaders")) {
                List<Map<String,String>> parameterList = (List<Map<String,String>>)coreMap.get("allowHeaders");
                List<String> resultList = new ArrayList<>();
                createCorsElement(parameterList, resultList);
                HttpCors.setAllowHeaders(resultList);
            }
            if (coreMap.containsKey("allowMethods")) {
                List<Map<String,String>> parameterList = (List<Map<String,String>>)coreMap.get("allowMethods");
                List<String> resultList = new ArrayList<>();
                createCorsElement(parameterList, resultList);
                List<HttpMethod> methodList = new ArrayList<>();
                for (String temp : resultList) {
                    methodList.add(HttpMethod.valueOf(temp.toUpperCase()));
                }
                HttpCors.setAllowMethods(methodList);
            }
            if (coreMap.containsKey("exposeHeaders")) {
                List<Map<String,String>> parameterList = (List<Map<String,String>>)coreMap.get("exposeHeaders");
                List<String> resultList = new ArrayList<>();
                createCorsElement(parameterList, resultList);
                HttpCors.setExposeHeaders(resultList);
            }
        }
    }
    private void createCorsElement(List<Map<String,String>> parameterList, List<String>resultList) {
        for (Map<String, String> parameter : parameterList) {
            String value = parameter.get("value");
            if (StringUtil.isNotEmpty(value)) {
                resultList.add(value.trim());
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void createHttpHtmlObject(Map<String, Object> dataMap) {
        if (dataMap.containsKey("html")) {
            Map<String, Object> htmlMap = (Map<String, Object>)dataMap.get("html");
            if (htmlMap.containsKey("support")) {
                Map<String, String> htmlSupportMap = new LinkedHashMap<>();
                List<Map<String, String>> elements = (List<Map<String, String>>)htmlMap.get("support");
                for (Map<String, String> element : elements) {
                    htmlSupportMap.put(element.get("fileType"), element.get("contentType"));
                }
                HttpHtml.setHtmlSupportMap(htmlSupportMap);
            }
            if (htmlMap.containsKey("cacheTime")) {
                HttpHtml.setCacheTime(htmlMap.get("cacheTime").toString());
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void createHttpProxyObject(Map<String, Object> dataMap) {
      //sever元素
        Map<String, HttpProxy> serverNameContextMap = new LinkedHashMap<>();
        Map<String, List<HttpProxy>> serverNameMap = new LinkedHashMap<>();
        if (dataMap.containsKey("proxy")) {
            List<Map<String, Map<String, Map<String, String>>>> list = (List<Map<String, Map<String, Map<String, String>>>>)dataMap.get("proxy");
            List<Map<String, Map<String, Map<String, String>>>> elements = list;
            for (Map<String, Map<String, Map<String, String>>> element : elements) {
                for (Map.Entry<String, Map<String, Map<String, String>>> elementName : element.entrySet()) {
                    String names = elementName.getKey();
                    String[] nameArray = names.split(";");//multiple hosts slip by [;]
                    for (String name : nameArray) {
                        List<HttpProxy> listFile = new ArrayList<>();
                        Map<String, Map<String, String>> contextMap = elementName.getValue();
                        for (Map.Entry<String, Map<String, String>> elementcontext : contextMap.entrySet()) {
                            String context = elementcontext.getKey();
                            Map<String, String> objMap = elementcontext.getValue();
                            String index = objMap.get("index");
                            String root = objMap.get("root");
                            String proxy = objMap.get("proxy");
                            String rewrite = objMap.get("rewrite");
                            String notFound = objMap.get("404");
                            String error = objMap.get("error");
                            HttpProxy httpServerFile = new HttpProxy();
                            httpServerFile.setName(name);
                            httpServerFile.setContext(context);
                            httpServerFile.setIndex(index);
                            httpServerFile.setRoot(root);
                            httpServerFile.setProxy(proxy);
                            if (StringUtil.isEmpty(rewrite)) {
                                httpServerFile.setRewrite(context);
                            } else {
                                httpServerFile.setRewrite(rewrite);
                            }
                            if (StringUtil.isNotEmpty(notFound)) {
                                httpServerFile.setNotFound(notFound);
                            } 
                            if (StringUtil.isNotEmpty(error)) {
                                httpServerFile.setError(error);
                            } 
                            listFile.add(httpServerFile);
                            serverNameContextMap.put(name+context, httpServerFile);
                        }
                        serverNameMap.put(name, listFile);
                    }
                }
            }
        }
        HttpProxy.setServerNameContextMap(serverNameContextMap);
        HttpProxy.setServerNameMap(serverNameMap);
    }
    
    public static void main(String[] args) throws Exception {
        new HttpServerConfigReader();
    }
}
