package com.polaris.container.gateway.proxy.tls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.pojo.HttpProtocolForTls;
import com.polaris.container.gateway.proxy.SslEngineSource;

public class SslEngineSourceFactory {

    private static SslEngineSource sslEngineSource = new SelfSignedSslALPNEngineSource();
    private static Logger logger = LoggerFactory.getLogger(SslEngineSourceFactory.class);
    public static SslEngineSource get() {
        if (HttpProtocolForTls.isTlsEnable()) {
            logger.info("开启TLS支持");
            return sslEngineSource;
        }
        return null;
    }
    
    public static void set(SslEngineSource inputSslEngineSource) {
        sslEngineSource = inputSslEngineSource;
    }
    
}
