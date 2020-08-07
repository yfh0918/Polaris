package com.polaris.container.gateway.proxy.websocket;

import com.polaris.container.gateway.HttpFileListener;
import com.polaris.container.gateway.HttpFileReader;
import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.core.pojo.KeyValuePair;
import com.polaris.core.util.PropertyUtil;
import com.polaris.core.util.StringUtil;

public class WsConfigReader implements HttpFileListener{
    
    private static String fileName = "gw_ws.txt";
    
    public static int WS_REQUEST_MAX_NMBER = 2000;
    
    static {
        new WsConfigReader();
    }
    
    @Override
    public void onChange(HttpFile file) {
        for (String conf : file.getData()) {
            KeyValuePair kv = PropertyUtil.getKVPair(conf);
            if (kv != null && StringUtil.isNotEmpty(kv.getValue())) {
                if ("websocket.request.maxNumber".equals(kv.getKey())) {
                    try {
                        WS_REQUEST_MAX_NMBER = Integer.parseInt(kv.getValue());
                    } catch (Exception ex) {}
                    
                }
            }
        }
    }
    
    public WsConfigReader() {
        HttpFileReader.INSTANCE.readFile(this, new HttpFile(fileName));
    }
}
