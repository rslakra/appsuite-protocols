package com.devamatre.appsuite.http.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Create an HostnameVerifier that hard-wires the expected hostname. Note that is different than the URL's hostname for example
 * <code>example.com</code> versus <code>example.org</code>.
 *
 * @author Rohtash Lakra
 * @version 1.0.0
 * @since 01/21/2024 4:21 PM
 */
public class AllHostVerifier implements HostnameVerifier {

    public AllHostVerifier() {
    }

    /*
     * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String,
     * javax.net.ssl.SSLSession)
     */
    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }

}
