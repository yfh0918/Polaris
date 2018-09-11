package com.polaris.gateway.util;

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
import org.littleshoot.proxy.SslEngineSource;

import com.polaris.comm.util.LogUtil;

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
public class GatewaySelfSignedSslEngineSource implements SslEngineSource {
	private static LogUtil logger = LogUtil.getInstance(GatewaySelfSignedSslEngineSource.class);

    public static final String KeyStoreType_STR = "JKS";
    public static final String ALIAS = "gateway";
    public static final String PASSWORD = "Tom.Yu";
    private static final String PROTOCOL = "TLS";
    public static final String crtFileName = "gateway.crt";
    public static final String jksKeyStoreFileName = "gateway.jks";
    public static final String p12KeyStoreFileName = "gateway.p12";
    private static String KEYALG = "EC";
    public File keyStoreFile;
    private final boolean trustAllServers;
    private final boolean sendCerts;
    private KeyStoreType keyStoreType;
    private SSLContext sslContext;

    /**
     * use exist keystore
     *
     * @param keyStorePath
     * @param trustAllServers
     * @param sendCerts
     */
    public GatewaySelfSignedSslEngineSource(String keyStorePath,
                                        boolean trustAllServers, boolean sendCerts, KeyStoreType keyStoreType) {
        JCEUtil.removeCryptographyRestrictions();
        this.trustAllServers = trustAllServers;
        this.sendCerts = sendCerts;
        this.keyStoreFile = new File(keyStorePath);
        this.keyStoreType = keyStoreType;
        initializeSSLContext();
    }

    /**
     * create keystore
     *
     * @param trustAllServers
     * @param sendCerts
     */
    @SuppressWarnings("static-access")
	public GatewaySelfSignedSslEngineSource(boolean trustAllServers, boolean sendCerts, String keyalg) {
        JCEUtil.removeCryptographyRestrictions();
        this.trustAllServers = trustAllServers;
        this.sendCerts = sendCerts;
        this.keyStoreType = KeyStoreType.JKS;
        this.KEYALG = keyalg;
        initializeKeyStore();
        initializeSSLContext();
    }

    public GatewaySelfSignedSslEngineSource() {
        this(false, true, KEYALG);
    }

    @Override
    public SSLEngine newSslEngine() {
        return sslContext.createSSLEngine();
    }

    @Override
    public SSLEngine newSslEngine(String peerHost, int peerPort) {
        return sslContext.createSSLEngine(peerHost, peerPort);
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public void initializeKeyStore() {
        File crtFile = new File(crtFileName);
        File jksFile = new File(jksKeyStoreFileName);
        File p12File = new File(p12KeyStoreFileName);
        keyStoreFile = jksFile;
        if (keyStoreFile.isFile()) {
            logger.info("Not deleting keystore");
            return;
        }
        CertificateInfo certificateInfo = new CertificateInfo();
        certificateInfo.countryCode("CN");
        certificateInfo.organization("tech-Tom.Yu.com");
        certificateInfo.email("yufenghua@tech-Tom.Yu.com");
        certificateInfo.commonName("Gateway Integration Certification Authority");
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
        rootCertificateGenerator.saveRootCertificateAndKey(KeyStoreType.JKS.name(), jksFile, ALIAS, PASSWORD);
        logger.info("JKS file created success");
        rootCertificateGenerator.saveRootCertificateAndKey(KeyStoreType.PKCS12.name(), p12File, ALIAS, PASSWORD);
        logger.info("PKCS12 file created success");
    }

    private void initializeSSLContext() {
        String algorithm = Security
                .getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }

        try {
            final KeyStore ks = KeyStore.getInstance(keyStoreType.name());
            ks.load(new FileInputStream(keyStoreFile), PASSWORD.toCharArray());

            // Set up key manager factory to use our key store
            final KeyManagerFactory kmf =
                    KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, PASSWORD.toCharArray());

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
