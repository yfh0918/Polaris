package com.polaris.container.gateway.proxy.tls;

import java.io.File;

import com.polaris.container.gateway.pojo.HttpProtocolHttp2;
import com.polaris.container.gateway.pojo.HttpProtocolTls;
import com.polaris.container.gateway.proxy.http2.Http2Exception;
import com.polaris.core.util.StringUtil;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolConfig.Protocol;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectedListenerFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectorFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class SelfSignedSslALPNContextFactory {
    private static SslContext sslContext = null;
    public static SslContext get() {
        if (sslContext == null) {
            synchronized(SslContext.class) {
                if (sslContext == null) {
                    sslContext = create();
                }
            }
        }
        return sslContext;
        
    }
    public static void set(SslContext inputSslContext) {
        sslContext = inputSslContext;
    }
    
    private static SslContext create() {
        
        try {
            String certificate = HttpProtocolTls.getCertificateFile();
            boolean isCertificate = false;
            File certificateFile = null;
            if (StringUtil.isNotEmpty(certificate)) {
                certificateFile = new File(certificate);
                if (certificateFile.isFile()) {
                    isCertificate = true;
                }
            }
            String privateKey = HttpProtocolTls.getPrivateKeyFile();
            boolean isPrivateKey = false;
            File privateKeyFile = null;
            if (StringUtil.isNotEmpty(privateKey)) {
                privateKeyFile = new File(privateKey);
                if (privateKeyFile.isFile()) {
                    isPrivateKey = true;
                }
            }
            if (!isCertificate || !isPrivateKey) {
                SelfSignedCertificate cert = new SelfSignedCertificate();
                certificateFile = cert.certificate();
                privateKeyFile = cert.privateKey();
            }
            String privateKeyPassword = HttpProtocolTls.getPrivateKeyPassword();
            if (StringUtil.isEmpty(privateKeyPassword)) {
                privateKeyPassword = null;
            }
            ApplicationProtocolConfig apn = new ApplicationProtocolConfig(
                    Protocol.ALPN,
                    // NO_ADVERTISE is currently the only mode supported by both OpenSsl and JDK providers.
                    SelectorFailureBehavior.NO_ADVERTISE,
                    // ACCEPT is currently the only mode supported by both OpenSsl and JDK providers.
                    SelectedListenerFailureBehavior.ACCEPT,
                    listProtocols());

            return SslContextBuilder.forServer(certificateFile, privateKeyFile, privateKeyPassword)
                                    .clientAuth(ClientAuth.NONE)
                                    .ciphers(SelfSignedSslALPNContextConstant.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                                    .applicationProtocolConfig(apn).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Http2Exception(ex.getMessage(), ex);
        }
    }
    
    private static String[] listProtocols() {
        if (HttpProtocolHttp2.isHttp20Enable()) {
            return new String[] {  ApplicationProtocolNames.HTTP_2 ,ApplicationProtocolNames.HTTP_1_1};        
        }
        return new String[] { ApplicationProtocolNames.HTTP_1_1 };
    }

}
