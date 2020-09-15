package com.polaris.container.gateway.pojo;

import com.polaris.core.util.StringUtil;

public abstract class HttpProtocolHttp2 {
    
    private static boolean HTTP20_ENABLE_INTIAL = true;
    private static boolean HTTP20_ENABLE = false;
    
    public static boolean isHttp20Enable() {
        init();
        return HTTP20_ENABLE;
    }
    
    private static void init() {
        if (HTTP20_ENABLE_INTIAL) {
            
            //enable
            String enable = HttpProtocol.getHttp20Map().get("enable");
            if (StringUtil.isNotEmpty(enable)) {
                try {
                    HTTP20_ENABLE = Boolean.parseBoolean(enable);
                } catch (Exception ex) {
                }
            } 
            
            HTTP20_ENABLE_INTIAL = false;
        }
    }
}
