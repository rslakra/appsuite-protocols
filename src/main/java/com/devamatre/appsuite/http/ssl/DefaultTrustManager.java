package com.devamatre.appsuite.http.ssl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author Rohtash Lakra
 * @version 1.0.0
 * @since 01/21/2024 4:19 PM
 */
public class DefaultTrustManager implements TrustManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTrustManager.class);
    private final X509TrustManager trustManager;

    public DefaultTrustManager(final X509TrustManager trustManager) {
        LOGGER.debug("X509TrustManagerImpl({})", trustManager);
        this.trustManager = trustManager;
    }

    /*
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    public X509Certificate[] getAcceptedIssuers() {
        return trustManager.getAcceptedIssuers();
    }

    /*
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.
     * security. cert.X509Certificate[], java.lang.String)
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        try {
            trustManager.checkClientTrusted(chain, authType);
        } catch (CertificateException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
    }

    /*
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.
     * security. cert.X509Certificate[], java.lang.String)
     */
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
        try {
            trustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
    }
}
