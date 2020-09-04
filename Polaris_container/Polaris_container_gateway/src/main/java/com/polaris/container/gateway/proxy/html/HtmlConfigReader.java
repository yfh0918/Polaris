package com.polaris.container.gateway.proxy.html;

import static io.netty.handler.codec.http.HttpMethod.GET;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.net.HostAndPort;
import com.polaris.container.gateway.HttpFileListener;
import com.polaris.container.gateway.HttpFileReader;
import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.container.gateway.pojo.HttpHtml;
import com.polaris.container.gateway.proxy.impl.ProxyUtils;
import com.polaris.core.pojo.KeyValuePair;
import com.polaris.core.util.PropertyUtil;
import com.polaris.core.util.StringUtil;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

public class HtmlConfigReader implements HttpFileListener{
    
    private static String fileName = "gw_html.txt";
    
    private static String SLIP = ":";
    
    private static String defaultStartupName = "index.html";
    
    private static Map<String, String> startupMap = new HashMap<>();

    private static Map<String, String> diskFilePathMap = new HashMap<>();

    private static Map<String, String> contentTypeMap = new HashMap<>();
    private static Map<String, String> defaultContentTypeMap = new HashMap<>();
    static {
        defaultContentTypeMap.put("js", "application/javascript");
        defaultContentTypeMap.put("css", "text/css");
        defaultContentTypeMap.put("jpe", "image/jpeg");
        defaultContentTypeMap.put("jpeg", "image/jpeg");
        defaultContentTypeMap.put("jpg", "image/jpeg");
        defaultContentTypeMap.put("gif", "image/gif");
        defaultContentTypeMap.put("png", "image/x-portable-anymap");
        defaultContentTypeMap.put("ppm", "image/x-portable-pixmap");
        defaultContentTypeMap.put("tif", "image/tiff");
        defaultContentTypeMap.put("tiff","image/tiff");
        defaultContentTypeMap.put("xht", "application/xhtml+xml");
        defaultContentTypeMap.put("xhtml","application/xhtml+xml");
        defaultContentTypeMap.put("html", "text/html");
        defaultContentTypeMap.put("htm", "text/html");

     }
    
    static {
        new HtmlConfigReader();
    }
    
    @Override
    public void onChange(HttpFile file) {
        Map<String, String> tempContentTypeMap = new HashMap<>();
        Map<String, String> tempStartupMap = new HashMap<>();
        Map<String, String> tempDiskFilePathMap = new HashMap<>();
        tempContentTypeMap.putAll(defaultContentTypeMap);
        
        for (String conf : file.getData()) {
            KeyValuePair kv = PropertyUtil.getKVPair(conf);
            if (kv != null && StringUtil.isNotEmpty(kv.getValue())) {
                if ("html.separator".equals(kv.getKey())) {
                    if (StringUtil.isNotEmpty(kv.getValue())) {
                        SLIP = kv.getValue();
                    }
                }
                if ("html.file.type.mapping".equals(kv.getKey())) {
                    if (StringUtil.isNotEmpty(kv.getValue())) {
                        String[] type = kv.getValue().split(SLIP);
                        if (type != null && type.length == 2) {
                            tempContentTypeMap.put(type[0], type[1]);
                        }
                    }
                }
                if ("html.startup.mapping".equals(kv.getKey())) {
                    if (StringUtil.isNotEmpty(kv.getValue())) {
                        String[] type = kv.getValue().split(SLIP);
                        if (type != null && type.length == 2) {
                            tempStartupMap.put(type[0], type[1]);
                        }
                    }
                }
                if ("html.disk.filepath.mapping".equals(kv.getKey())) {
                    if (StringUtil.isNotEmpty(kv.getValue())) {
                        String[] type = kv.getValue().split(SLIP);
                        if (type != null && type.length == 2) {
                            tempDiskFilePathMap.put(type[0], type[1]);
                        }
                    }
                }
            }
        }
        contentTypeMap = tempContentTypeMap;
        startupMap = tempStartupMap;
        diskFilePathMap = tempDiskFilePathMap;
    }
    
    public HtmlConfigReader() {
        HttpFileReader.INSTANCE.readFile(this, new HttpFile(fileName));
    }
    
    public static String getStartupFile(String host) {
        String startup = startupMap.get(host);
        if (StringUtil.isEmpty(startup)) {
            return defaultStartupName;
        }
        return startup;
    }
    public static String getDiskFilePath(String host) {
        return diskFilePathMap.get(host);
    }
    
    public static HttpHtml getHtml(HttpRequest request) throws Exception{
        if (!GET.equals(request.method())) {
            return null;
        }
        String uri = request.uri();
        String contentType = null;
        String startup = null;
        String host = null;
        String fileType = null;
        if (uri == null || StringUtil.isEmpty(uri) || uri.equals("/")) {
            host = HtmlConfigReader.getHostAndPort(HtmlConfigReader.identifyHostAndPort(request)).getHost();
            startup = HtmlConfigReader.getStartupFile(host);
            uri = "/";
            fileType = getFileType(startup);
        } else {
            fileType = getFileType(uri);
        }
        contentType = contentTypeMap.get(fileType.toLowerCase());
        if (contentType == null) {
            return null;
        }
        if (host == null) {
            host = HtmlConfigReader.getHostAndPort(HtmlConfigReader.identifyHostAndPort(request)).getHost();
        }
        if (startup == null) {
            startup = HtmlConfigReader.getStartupFile(host);
        }
        String diskPath = HtmlConfigReader.getDiskFilePath(host);
        HttpHtml htmlObj = new HttpHtml();
        htmlObj.setContentType(contentType);
        htmlObj.setFilePath(diskPath);
        htmlObj.setFileType(fileType);
        htmlObj.setHost(host);
        htmlObj.setUri(uri);
        htmlObj.setStartup(startup);
        return htmlObj;
    }
    
    private static String getFileType(String uri) {
        if (StringUtil.isNotEmpty(uri)) {
            int index = uri.lastIndexOf(".");
            if (index > 0) {
                return uri.substring(index + 1).trim();
            } 
        }
        return "";
    }
    
    private static String identifyHostAndPort(HttpRequest httpRequest) {
        String hostAndPort = ProxyUtils.parseHostAndPort(httpRequest);
        if (StringUtils.isBlank(hostAndPort)) {
            List<String> hosts = httpRequest.headers().getAll(
                    HttpHeaderNames.HOST.toString());
            if (hosts != null && !hosts.isEmpty()) {
                hostAndPort = hosts.get(0);
            }
        }
        return hostAndPort;
    }
    
    private static HostAndPort getHostAndPort(String hostAndPort)
            throws UnknownHostException {
        try {
            return HostAndPort.fromString(hostAndPort);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
