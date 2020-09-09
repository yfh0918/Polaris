package com.polaris.container.gateway.pojo;

import com.polaris.core.util.StringUtil;

public abstract class HttpProtocolTls {
    private static boolean INITIAL = true;

    private static boolean TLS_ENABLE = false;
    
    private static String CERTIFICATE_FILE = null;
    private static String PRIVATE_KEY_FILE = null;
    private static String PRIVATE_KEY_PASSWORD = null;
    

    
    public static boolean isTlsEnable() {
        init();
        return TLS_ENABLE;
    }
    
    public static String getCertificateFile() {
        init();
        return CERTIFICATE_FILE;
    }
    
    public static String getPrivateKeyFile() {
        init();
        return PRIVATE_KEY_FILE;
    }
    
    public static String getPrivateKeyPassword() {
        init();
        return PRIVATE_KEY_PASSWORD;
    }
    

    
    private static void init() {
        if (INITIAL) {
            String enable = HttpProtocol.getTlsMap().get("enable");
            if (StringUtil.isNotEmpty(enable)) {
                try {
                    TLS_ENABLE = Boolean.parseBoolean(enable);
                } catch (Exception ex) {
                }
            }
            
            String certificateFile = HttpProtocol.getTlsMap().get("certificateFile");
            if (StringUtil.isNotEmpty(certificateFile)) {
                CERTIFICATE_FILE = certificateFile;
            }
            String privateKeyFile = HttpProtocol.getTlsMap().get("privateKeyFile");
            if (StringUtil.isNotEmpty(privateKeyFile)) {
                PRIVATE_KEY_FILE = privateKeyFile;
            }
            String privateKeyPassword = HttpProtocol.getTlsMap().get("privateKeyPassword");
            if (StringUtil.isNotEmpty(privateKeyPassword)) {
                PRIVATE_KEY_PASSWORD = privateKeyPassword;
            }
            
            
            
          INITIAL = false;
        }
    }
}
