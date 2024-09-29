package com.rslakra.appsuite.http.rest;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * @author Rohtash Lakra
 * @created 4/13/20 6:26 PM
 */
public class AuthConfig {

    private static final String HTTPS = "https";
    private static final int SSL_PORT = 443;
    private static final Scheme DEFAULT_SCHEME = new Scheme(HTTPS, SSL_PORT, null);

    private RestBuilder restBuilder;

    /**
     * @param restBuilder
     */
    public AuthConfig(final RestBuilder restBuilder) {
        this.restBuilder = restBuilder;
    }

    /**
     * @return
     */
    protected RestBuilder getBuilder() {
        return getBuilder();
    }

    /**
     * @param user
     * @param pass
     */
    public void basic(String user, String pass) {
        URI uri = ((UriBuilder) getBuilder().getUri()).toURI();
        if (uri == null) {
            throw new IllegalStateException("a default URI must be set");
        } else {
            this.basic(uri.getHost(), uri.getPort(), user, pass);
        }
    }

    /**
     * @param host
     * @param port
     * @param user
     * @param pass
     */
    public void basic(String host, int port, String user, String pass) {
        HttpClient client = getBuilder().getHttpClient();
        if (!(client instanceof AbstractHttpClient)) {
            throw new IllegalStateException("client is not an AbstractHttpClient");
        } else {
            ((AbstractHttpClient) client).getCredentialsProvider()
                .setCredentials(new AuthScope(host, port), new UsernamePasswordCredentials(user, pass));
        }
    }

    /**
     * @param user
     * @param pass
     * @param workstation
     * @param domain
     */
    public void ntlm(String user, String pass, String workstation, String domain) {
        URI uri = ((UriBuilder) getBuilder().getUri()).toURI();
        if (uri == null) {
            throw new IllegalStateException("a default URI must be set");
        } else {
            this.ntlm(uri.getHost(), uri.getPort(), user, pass, workstation, domain);
        }
    }

    /**
     * @param host
     * @param port
     * @param user
     * @param pass
     * @param workstation
     * @param domain
     */
    public void ntlm(String host, int port, String user, String pass, String workstation, String domain) {
        HttpClient client = getBuilder().getHttpClient();
        if (!(client instanceof AbstractHttpClient)) {
            throw new IllegalStateException("client is not an AbstractHttpClient");
        } else {
            ((AbstractHttpClient) client).getCredentialsProvider()
                .setCredentials(new AuthScope(host, port), new NTCredentials(user, pass, workstation, domain));
        }
    }

    /**
     * @param certUrl
     * @param password
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void certificate(String certUrl, String password) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream jksStream = (new URL(certUrl)).openStream();

        try {
            keyStore.load(jksStream, password.toCharArray());
        } finally {
            jksStream.close();
        }

        SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore, password);
        socketFactory.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
        getBuilder().getHttpClient()
            .getConnectionManager()
            .getSchemeRegistry()
            .register(new Scheme(HTTPS, socketFactory, SSL_PORT));
    }

    /**
     * @param consumerKey
     * @param consumerSecret
     * @param accessToken
     * @param secretToken
     */
    public void oauth(String consumerKey, String consumerSecret, String accessToken, String secretToken) {
        HttpClient client = getBuilder().getHttpClient();
        if (!(client instanceof AbstractHttpClient)) {
            throw new IllegalStateException("client is not an AbstractHttpClient");
        } else {
            AbstractHttpClient httpClient = ((AbstractHttpClient) client);
            httpClient.removeRequestInterceptorByClass(AuthConfig.OAuthSigner.class);
            if (consumerKey != null) {
                httpClient.addRequestInterceptor(
                    new OAuthSigner(consumerKey, consumerSecret, accessToken, secretToken));
            }
        }
    }

    /**
     * OAuthSigner request interceptor
     */
    private static class OAuthSigner implements HttpRequestInterceptor {

        private String consumerKey;
        private String consumerSecret;
        private String accessToken;
        private String secretToken;

//        protected OAuthConsumer oauth;

        /**
         * @param consumerKey
         * @param consumerSecret
         * @param accessToken
         * @param secretToken
         */
        public OAuthSigner(String consumerKey, String consumerSecret, String accessToken, String secretToken) {
            this.consumerKey = consumerKey;
            this.consumerSecret = consumerSecret;
            this.accessToken = accessToken;
            this.secretToken = secretToken;
//            this.oauth = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
//            this.oauth.setTokenWithSecret(accessToken, secretToken);
        }
//
//        public void process(HttpRequest request, HttpContext ctx) throws HttpException, IOException {
//            try {
//                HttpHost host = (HttpHost) ctx.getAttribute("rest.target_host");
//                URI requestURI = (new URI(host.toURI())).resolve(request.getRequestLine().getUri());
//                oauth.signpost.rest.HttpRequest
//                    oAuthRequest =
//                    new AuthConfig.OAuthSigner.OAuthRequestAdapter(request, requestURI);
//                this.oauth.sign(oAuthRequest);
//            } catch (URISyntaxException var6) {
//                throw new HttpException("Error rebuilding request URI", var6);
//            } catch (OAuthException var7) {
//                throw new HttpException("OAuth signing error", var7);
//            }
//        }
//
//        static class OAuthRequestAdapter implements oauth.signpost.rest.HttpRequest {
//
//            final HttpRequest request;
//            final URI requestURI;
//
//            OAuthRequestAdapter(HttpRequest request, URI requestURI) {
//                this.request = request;
//                this.requestURI = requestURI;
//            }
//
//            public String getRequestUrl() {
//                return this.requestURI.toString();
//            }
//
//            public void setRequestUrl(String url) {
//            }
//
//            public Map<String, String> getAllHeaders() {
//                return Arrays.asList(this.request.getAllHeaders()).stream().collect(Collectors.toMap(Header::getName,
//                                                                                                     Header::getValue));
//            }
//
//            /**
//             * @return
//             */
//            public String getContentType() {
//                return getHeader(HttpHeaders.CONTENT_TYPE);
//            }
//
//            /**
//             * Returns the value of the given header.
//             *
//             * @param name
//             * @return
//             */
//            public String getHeader(final String name) {
//                Header header = this.request.getFirstHeader(name);
//                return (header == null ? null : header.getValue());
//            }
//
//            public InputStream getMessagePayload() throws IOException {
//                return this.request instanceof HttpEntityEnclosingRequest ? ((HttpEntityEnclosingRequest) this.request)
//                    .getEntity().getContent() : null;
//            }
//
//            public String getMethod() {
//                return this.request.getRequestLine().getMethod();
//            }
//
//            public void setHeader(String key, String value) {
//                this.request.setHeader(key, value);
//            }
//
//            public Object unwrap() {
//                return this.request;
//            }
//        }

        /**
         * @param httpRequest the request to preprocess
         * @param httpContext the context for the request
         * @throws HttpException
         * @throws IOException
         */
        @Override
        public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {

        }
    }
}
