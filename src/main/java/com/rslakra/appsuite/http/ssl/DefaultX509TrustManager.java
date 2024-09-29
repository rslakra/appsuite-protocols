package com.rslakra.appsuite.http.ssl;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author Rohtash Lakra
 * @version 1.0.0
 * @since 2/28/20 10:16 AM
 */
public class DefaultX509TrustManager extends AbstractX509TrustManager implements X509TrustManager {

    /**
     * @param chain    the peer certificate chain
     * @param authType the authentication type based on the client certificate
     * @throws CertificateException
     */
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    /**
     * @param chain    the peer certificate chain
     * @param authType the key exchange algorithm used
     * @throws CertificateException
     */
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    /**
     * @return
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
//                return new X509Certificate[0];
        return new java.security.cert.X509Certificate[]{};
    }
}
