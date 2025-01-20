package com.rslakra.appsuite.protocol.http.ssl;

import com.rslakra.appsuite.core.IOUtils;
import com.rslakra.appsuite.core.security.GuardUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * Creates the generic SSL factory.
 *
 * @author Rohtash Lakra
 * @version 1.0.0
 * @since 04/12/2017 05:17:15 PM
 */
public enum SSLFactory {

    INSTANCE;

    /* PKCS12 */
    public static final String PKCS12 = "PKCS12";

    /* JKS */
    public static final String JKS = "JKS";

    /* TLS_VER_1 */
    public static final String TLS_VER_1 = "TLSv1";

    /* TLS_VER_2 */
    public static final String TLS_VER_2 = "TLSv2";

    /* SUN_X509 */
    public static final String SUN_X509 = "SunX509";

    /* trustAllSSLSocketFactory */
    private SSLSocketFactory trustAllSSLSocketFactory;

    /* hostNameVerifier */
    private HostnameVerifier hostNameVerifier;

    /**
     * Returns the SSL Context.
     *
     * @param tlsVersion
     * @param keyManagers
     * @param trustManagers
     * @param secureRandom
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public SSLContext getSSLContext(String tlsVersion, KeyManager[] keyManagers, TrustManager[] trustManagers, SecureRandom secureRandom) throws NoSuchAlgorithmException, KeyManagementException {
        System.out.println("+getSSLContext(" + tlsVersion + ", " + keyManagers + ", " + trustManagers + ", " + secureRandom + "):");

        SSLContext sslContext = SSLContext.getInstance(tlsVersion);
        sslContext.init(keyManagers, trustManagers, secureRandom);

        System.out.println("-getSSLContext(), sslContext:" + sslContext);
        return sslContext;
    }

    /**
     * Returns the SSL Context.
     *
     * @param tlsVersion
     * @param keyManagers
     * @param trustManagers
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public SSLContext getSSLContext(String tlsVersion, KeyManager[] keyManagers, TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyManagementException {
        return getSSLContext(tlsVersion, null, trustManagers, null);
    }

    /**
     * Returns the SSL Context.
     *
     * @param tlsVersion
     * @param trustManagers
     * @param secureRandom
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public SSLContext getSSLContext(String tlsVersion, TrustManager[] trustManagers, SecureRandom secureRandom) throws NoSuchAlgorithmException, KeyManagementException {
        return getSSLContext(tlsVersion, null, trustManagers, secureRandom);
    }

    /**
     * Creates the SSL Socket Factory.
     *
     * @return
     * @throws Exception
     */
    private SSLSocketFactory createTrustAllSSLSocketFactory() throws Exception {
        TrustManager[] trustEveryone = new TrustManager[]{new DefaultX509TrustManager()};

        // Create an SSLContext that uses our TrustManager
        SSLContext sslContext = getSSLContext("TLS", trustEveryone, GuardUtils.newSecureRandom());

        // use a SocketFactory from our SSLContext
        return sslContext.getSocketFactory();
    }

    /**
     * Returns the SSL Socket Factory.
     *
     * @return
     */
    public SSLSocketFactory getTrustAllSSLSocketFactory() {
        if (trustAllSSLSocketFactory == null) {
            try {
                trustAllSSLSocketFactory = createTrustAllSSLSocketFactory();
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }

        return trustAllSSLSocketFactory;
    }

    /**
     * Returns the trustManagerFactory;
     *
     * @param trustKeyStore
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public TrustManager[] getTrustManagers(final KeyStore trustKeyStore) throws NoSuchAlgorithmException, KeyStoreException {
        System.out.println("+getTrustManagers(" + trustKeyStore + ")");
        TrustManager[] trustManagers = null;

        // Create a TrustManager that trusts the CAs in our KeyStore
        String trustFactoryAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        System.out.println("trustFactoryAlgorithm:" + trustFactoryAlgorithm);
        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(trustFactoryAlgorithm);
        trustFactory.init(trustKeyStore);
        trustManagers = trustFactory.getTrustManagers();

        System.out.println("-createTrustManagerFactory(), trustManagers:" + trustManagers);
        return trustManagers;
    }

    /**
     * Creates the SSL Socket Factory.
     *
     * @param certInputStream
     * @param secureRandom
     * @return
     * @throws Exception
     */
    private SSLSocketFactory createTrustSSLSocketFactory(InputStream certInputStream, SecureRandom secureRandom) throws Exception {
        X509Certificate certificate = GuardUtils.newX509Certificate(certInputStream, true);

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        System.out.println("keyStoreType:" + keyStoreType);
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        String alias = certificate.getSubjectX500Principal().getName();
        keyStore.setCertificateEntry(alias, certificate);

        // Create a TrustManager that trusts the CAs in our KeyStore
        final TrustManager[] trustManagers = getTrustManagers(keyStore);
        DefaultX509TrustManager defaultX509TrustManager;
        final X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
        final TrustManager[] wrappedTrustManagers = new TrustManager[]{new DefaultTrustManager(trustManager)};
        // Create an SSLContext that uses our TrustManager
        SSLContext sslContext = getSSLContext(TLS_VER_1, wrappedTrustManagers, secureRandom);

        // use a SocketFactory from our SSLContext
        return sslContext.getSocketFactory();
    }

    /**
     * Returns the HostnameVerifier.
     *
     * @return
     */
    public HostnameVerifier getHostNameVerifier() {
        if (hostNameVerifier == null) {
            hostNameVerifier = new AllHostVerifier();
        }

        return hostNameVerifier;
    }

    /**
     * The SSL socket factor to be set.
     *
     * @param urlConnection
     */
    public void setSSLSocketFactory(HttpURLConnection urlConnection) {
        if (urlConnection instanceof HttpsURLConnection) {
            SSLSocketFactory sslSocketFactory = null;
            sslSocketFactory = getTrustAllSSLSocketFactory();
            ((HttpsURLConnection) urlConnection).setSSLSocketFactory(sslSocketFactory);
            ((HttpsURLConnection) urlConnection).setHostnameVerifier(getHostNameVerifier());
        }
    }

    /**
     * Produces a KeyStore from a PKCS12 (.p12) certificate file, typically the client certificate
     *
     * @param p12CertInputStream
     * @param p12CertPass
     * @param closeStream
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public KeyStore loadPKCS12KeyStore(InputStream p12CertInputStream, char[] p12CertPass, boolean closeStream) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(PKCS12);
        keyStore.load(p12CertInputStream, p12CertPass);
        if (closeStream) {
            IOUtils.closeSilently(p12CertInputStream);
        }

        return keyStore;
    }

    /**
     * Produces a KeyStore from a PKCS12 (.p12) certificate file, typically the client certificate
     *
     * @param p12CertFileName
     * @param p12CertPassword
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public KeyStore loadPKCS12KeyStore(String p12CertFileName, String p12CertPassword) throws GeneralSecurityException, IOException {
        return loadPKCS12KeyStore(IOUtils.toInputStream(IOUtils.readBytes(p12CertFileName)), p12CertPassword.toCharArray(), false);
    }

    /**
     * Reads and decodes a base-64 encoded DER certificate (a .pem certificate), typically the server's CA
     * certificate.
     *
     * @param pemCertificateStream
     * @return
     * @throws IOException
     */
    public byte[] loadPEMCertificate(InputStream pemCertificateStream) throws IOException {
        byte[] pemDecodedBytes = null;
        BufferedReader bufferedReader = null;
        try {
            final StringBuilder pemBuilder = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(pemCertificateStream));
            String line = bufferedReader.readLine();
            while (line != null) {
                if (!line.startsWith("--")) {
                    pemBuilder.append(line);
                }
                line = bufferedReader.readLine();
            }

            pemDecodedBytes = Base64.getDecoder().decode(pemBuilder.toString());
        } finally {
            IOUtils.closeSilently(bufferedReader);
        }

        return pemDecodedBytes;
    }

    /**
     * Creates an SSLContext with the client and server certificates
     *
     * @param p12CertFileName A File containing the client certificate
     * @param p12CertPassword Password for the client certificate
     * @param caCertString    A String containing the server certificate
     * @return An initialized SSLContext
     * @throws Exception
     */
    private SSLContext createSSLContext(String p12CertFileName, String p12CertPassword, String caCertString) throws Exception {
        final KeyStore keyStore = loadPKCS12KeyStore(p12CertFileName, p12CertPassword);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
        kmf.init(keyStore, p12CertPassword.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();

        final KeyStore trustStore = loadPEMTrustStore(caCertString);
        TrustManager[] trustManagers = {new CustomX509TrustManager(trustStore)};

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);

        return sslContext;
    }

    /**
     * Produces a KeyStore from a String containing a PEM certificate (typically, the server's CA certificate)
     *
     * @param certificateString A String containing the PEM-encoded certificate
     * @return a KeyStore (to be used as a trust store) that contains the certificate
     * @throws Exception
     */
    private KeyStore loadPEMTrustStore(String certificateString) throws Exception {
        byte[] pemDecodedBytes = loadPEMCertificate(new ByteArrayInputStream(certificateString.getBytes()));
        ByteArrayInputStream derInputStream = new ByteArrayInputStream(pemDecodedBytes);

        X509Certificate x509Certificate = GuardUtils.newX509Certificate(derInputStream, true);
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null);
        String alias = x509Certificate.getSubjectX500Principal().getName();
        trustStore.setCertificateEntry(alias, x509Certificate);

        return trustStore;
    }

}
