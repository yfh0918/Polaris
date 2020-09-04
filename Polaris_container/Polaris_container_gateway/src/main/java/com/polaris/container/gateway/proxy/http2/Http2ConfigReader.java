package com.polaris.container.gateway.proxy.http2;

import com.polaris.container.gateway.HttpFileListener;
import com.polaris.container.gateway.HttpFileReader;
import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.core.pojo.KeyValuePair;
import com.polaris.core.util.PropertyUtil;
import com.polaris.core.util.StringUtil;

public class Http2ConfigReader implements HttpFileListener{
    
    private static String fileName = "gw_http2.txt";
    
    private static int H2_MAX_CONTENT_LENGTH = 65536;
    
    static {
        new Http2ConfigReader();
    }
    
    @Override
    public void onChange(HttpFile file) {
        for (String conf : file.getData()) {
            KeyValuePair kv = PropertyUtil.getKVPair(conf);
            if (kv != null && StringUtil.isNotEmpty(kv.getValue())) {
                if ("http2.maxContentLength".equals(kv.getKey())) {
                    try {
                        H2_MAX_CONTENT_LENGTH = Integer.parseInt(kv.getValue());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    
    public Http2ConfigReader() {
        HttpFileReader.INSTANCE.readFile(this, new HttpFile(fileName));
    }
    

    public static int getMaxContentLength() {
        return H2_MAX_CONTENT_LENGTH;
    }
}
