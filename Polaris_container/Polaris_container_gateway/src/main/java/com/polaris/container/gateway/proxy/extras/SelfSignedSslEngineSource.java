package com.polaris.container.gateway.proxy.extras;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.proxy.SslEngineSource;
import com.polaris.container.gateway.util.JCEUtil;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.StringUtil;

import io.netty.buffer.ByteBufAllocator;
import net.lightbody.bmp.mitm.CertificateInfo;
import net.lightbody.bmp.mitm.RootCertificateGenerator;
import net.lightbody.bmp.mitm.keys.ECKeyGenerator;
import net.lightbody.bmp.mitm.keys.RSAKeyGenerator;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class SelfSignedSslEngineSource implements SslEngineSource {
	private static Logger logger = LoggerFactory.getLogger(SelfSignedSslEngineSource.class);

    public static final String KeyStoreType_STR = "JKS";
    private static final String PROTOCOL = "TLS";
    private static String KEYALG = "EC";
    public File keyStoreFile;
    private final boolean trustAllServers;
    private final boolean sendCerts;
    private KeyStoreType keyStoreType;
    private SSLContext sslContext;

    /**
     * create keystore
     *
     * @param trustAllServers
     * @param sendCerts
     */
    @SuppressWarnings("static-access")
	public SelfSignedSslEngineSource(boolean trustAllServers, boolean sendCerts, String keyalg) {
        JCEUtil.removeCryptographyRestrictions();
        this.trustAllServers = trustAllServers;
        this.sendCerts = sendCerts;
        if ("jks".equals(ConfClient.get("server.tls.style"))) {
            this.keyStoreType = KeyStoreType.JKS;
        } else {
            this.keyStoreType = KeyStoreType.PKCS12;
        }
        
        //是否外部导入的证书
        String keystore = ConfClient.get("server.tls.keystore");
        boolean isFile = false;
        File tempKeystoreFile = null;
        if (StringUtil.isNotEmpty(keystore)) {
        	tempKeystoreFile = new File(keystore);
        	if (tempKeystoreFile.isFile()) {
        		isFile = true;
        	}
        }
        
        //自己生成证书
        if (!isFile) {
            this.KEYALG = keyalg;
            initializeKeyStore();
        } else {
        	
        	//外部导入证书
        	this.keyStoreFile = tempKeystoreFile;
        }
        
        //初始化ssl
        initializeSSLContext();
    }

    public SelfSignedSslEngineSource() {
        this(false, true, KEYALG);
    }

    @Override
    public SSLEngine newSslEngine(ByteBufAllocator alloc) {
        return engineWapper(sslContext.createSSLEngine());
    }

    @Override
    public SSLEngine newSslEngine(ByteBufAllocator alloc, String peerHost, int peerPort) {
        return engineWapper(sslContext.createSSLEngine(peerHost, peerPort));
    }
    
    private SSLEngine engineWapper(SSLEngine sslEngine) {
        sslEngine.setNeedClientAuth(false);
        sslEngine.setUseClientMode(false);
        return sslEngine;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public void initializeKeyStore() {
        File crtFile = new File(ConfClient.get("server.tls.alias") + ".crt");
        if ("jks".equals(ConfClient.get("server.tls.style"))) {
            File jksFile = new File(ConfClient.get("server.tls.alias") + ".jks");
            keyStoreFile = jksFile;
        } else {
            File p12File = new File(ConfClient.get("server.tls.alias") + ".p12");
            keyStoreFile = p12File;
        }
        if (keyStoreFile.isFile()) {
            logger.info("Not deleting keystore");
            return;
        }
        CertificateInfo certificateInfo = new CertificateInfo();
        certificateInfo.countryCode(ConfClient.get("server.certificate.country"));
        certificateInfo.organization(ConfClient.get("server.certificate.organization"));
        certificateInfo.email(ConfClient.get("server.certificate.email"));
        certificateInfo.commonName(ConfClient.get("server.certificate.name"));
        DateTime dateTime = new DateTime();
        certificateInfo.notBefore(dateTime.minusDays(1).toDate());
        certificateInfo.notAfter(dateTime.plusYears(1).toDate());
        RootCertificateGenerator.Builder rootCertificateGeneratorBuilder = RootCertificateGenerator.builder();
        rootCertificateGeneratorBuilder.certificateInfo(certificateInfo);
        RootCertificateGenerator rootCertificateGenerator;
        if (KEYALG.equals("RSA")) {
            rootCertificateGenerator = rootCertificateGeneratorBuilder.keyGenerator(new RSAKeyGenerator()).build();
        } else {
            rootCertificateGenerator = rootCertificateGeneratorBuilder.keyGenerator(new ECKeyGenerator()).build();
        }

        rootCertificateGenerator.saveRootCertificateAsPemFile(crtFile);
        logger.info("CRT file created success");
        if ("jks".equals(ConfClient.get("server.tls.style"))) {
            rootCertificateGenerator.saveRootCertificateAndKey(KeyStoreType.JKS.name(), keyStoreFile, ConfClient.get("server.tls.alias"), ConfClient.get("server.tls.password"));
            logger.info("JKS file created success");
        } else {
            rootCertificateGenerator.saveRootCertificateAndKey(KeyStoreType.PKCS12.name(), keyStoreFile, ConfClient.get("server.tls.alias"), ConfClient.get("server.tls.password"));
            logger.info("PKCS12 file created success");
        }
    }

    private void initializeSSLContext() {
        String algorithm = Security
                .getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }

        try {
            final KeyStore ks = KeyStore.getInstance(keyStoreType.name());
            ks.load(new FileInputStream(keyStoreFile), ConfClient.get("server.tls.password").toCharArray());

            // Set up key manager factory to use our key store
            final KeyManagerFactory kmf =
                    KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, ConfClient.get("server.tls.password").toCharArray());

            // Set up a trust manager factory to use our key store
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(algorithm);
            tmf.init(ks);

            TrustManager[] trustManagers = null;
            if (!trustAllServers) {
                trustManagers = tmf.getTrustManagers();
            } else {
                trustManagers = new TrustManager[]{new X509TrustManager() {
                    // TrustManager that trusts all servers
                    @Override
                    public void checkClientTrusted(X509Certificate[] arg0,
                                                   String arg1)
                            throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] arg0,
                                                   String arg1)
                            throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }};
            }

            KeyManager[] keyManagers = null;
            if (sendCerts) {
                keyManagers = kmf.getKeyManagers();
            } else {
                keyManagers = new KeyManager[0];
            }

            // Initialize the SSLContext to work with our key managers.
            sslContext = SSLContext.getInstance(PROTOCOL);
            sslContext.init(keyManagers, trustManagers, null);
        } catch (final Exception e) {
            throw new Error(
                    "Failed to initialize the server-side SSLContext", e);
        }
    }

    public enum KeyStoreType {
        JKS, PKCS12;
    }

}
