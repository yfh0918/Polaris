package com.polaris.container.gateway.proxy.extras;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.proxy.SslEngineSource;
import com.polaris.core.config.ConfClient;

public class SslEngineSourceFactory {

    private static SslEngineSource sslEngineSource = new SelfSignedSslALPNEngineSource();
    private static Logger logger = LoggerFactory.getLogger(SslEngineSourceFactory.class);
    public static SslEngineSource get() {
        boolean tls = Boolean.parseBoolean(ConfClient.get("server.tls.enable","false"));
        if (tls) {
            logger.info("开启TLS支持");
            return sslEngineSource;
        }
        return null;
    }
    
    public static void set(SslEngineSource inputSslEngineSource) {
        sslEngineSource = inputSslEngineSource;
    }
    
}
