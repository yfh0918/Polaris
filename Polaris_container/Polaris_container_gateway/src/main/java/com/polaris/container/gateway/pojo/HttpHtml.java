package com.polaris.container.gateway.pojo;

import java.util.HashMap;
import java.util.Map;

public class HttpHtml {
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
    private volatile static Map<String, String> htmlSupportMap = new HashMap<>();
    public static Map<String, String> getHtmlSupportMap() {
        return htmlSupportMap;
    }

    public static void setHtmlSupportMap(Map<String, String> htmlSupportMap) {
        for (Map.Entry<String, String> entry : defaultContentTypeMap.entrySet()) {
            htmlSupportMap.putIfAbsent(entry.getKey(), entry.getValue());
        }
        HttpHtml.htmlSupportMap = htmlSupportMap;
    }
}
