package com.rslakra.appsuite.protocol.http.rest;

import com.rslakra.appsuite.core.BeanUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Rohtash Lakra
 * @created 4/13/20 6:28 PM
 */
public class UriBuilder implements Cloneable {

    public static final Charset UTF_8 = Charset.forName("utf-8");

    protected URI baseUri;

    /**
     * @param url
     * @throws URISyntaxException
     */
    public UriBuilder(String url) throws URISyntaxException {
        this(new URI(url));
    }

    /**
     * @param url
     * @throws URISyntaxException
     */
    public UriBuilder(URL url) throws URISyntaxException {
        this(url.toURI());
    }

    /**
     * @param uri
     * @throws IllegalArgumentException
     */
    public UriBuilder(URI uri) throws IllegalArgumentException {
        if (uri == null) {
            throw new IllegalArgumentException("uri must not be null!");
        } else {
            this.baseUri = uri;
        }
    }

    /**
     * @param uri
     * @return
     * @throws URISyntaxException
     */
    public static URI toUri(Object uri) throws URISyntaxException {
        if (uri instanceof URI) {
            return (URI) uri;
        } else if (uri instanceof URL) {
            return ((URL) uri).toURI();
        } else {
            return uri instanceof UriBuilder ? ((UriBuilder) uri).toURI() : new URI(uri.toString());
        }
    }

    /**
     * @param list
     * @param <T>
     * @return
     */
    public static <T> List<T> newList(final List<T> list) {
        return (Objects.isNull(list) ? null : new ArrayList<T>(list));
    }

    /**
     * @param scheme
     * @param userInfo
     * @param host
     * @param port
     * @param path
     * @param query
     * @param fragment
     * @return
     * @throws URISyntaxException
     */
    protected URI update(final String scheme, final String userInfo, final String host, final int port,
                         final String path, final String query, final String fragment) throws URISyntaxException {
        final URI uri = new URI(scheme, userInfo, host, port, this.baseUri.getPath(), (String) null, (String) null);
        final StringBuilder queryBuilder = new StringBuilder();
        if (path != null) {
            queryBuilder.append(path);
        }

        if (query != null) {
            queryBuilder.append("?").append(query);
        }

        if (fragment != null) {
            queryBuilder.append("#").append(fragment);
        }

        return uri.resolve(queryBuilder.toString());
    }

    /**
     * @param scheme
     * @return
     * @throws URISyntaxException
     */
    public UriBuilder setScheme(final String scheme) throws URISyntaxException {
        this.baseUri =
            this.update(scheme, this.baseUri.getUserInfo(), this.baseUri.getHost(), this.baseUri.getPort(),
                        this.baseUri.getRawPath(), this.baseUri.getRawQuery(), this.baseUri.getRawFragment());
        return this;
    }

    /**
     * @return
     */
    public String getScheme() {
        return this.baseUri.getScheme();
    }

    /**
     * @param port
     * @return
     * @throws URISyntaxException
     */
    public UriBuilder setPort(int port) throws URISyntaxException {
        this.baseUri =
            this.update(this.baseUri.getScheme(), this.baseUri.getUserInfo(), this.baseUri.getHost(), port,
                        this.baseUri.getRawPath(), this.baseUri.getRawQuery(), this.baseUri.getRawFragment());
        return this;
    }

    /**
     * @return
     */
    public int getPort() {
        return this.baseUri.getPort();
    }

    /**
     * @param host
     * @return
     * @throws URISyntaxException
     */
    public UriBuilder setHost(String host) throws URISyntaxException {
        this.baseUri =
            this.update(this.baseUri.getScheme(), this.baseUri.getUserInfo(), host, this.baseUri.getPort(),
                        this.baseUri.getRawPath(), this.baseUri.getRawQuery(), this.baseUri.getRawFragment());
        return this;
    }

    /**
     * @return
     */
    public String getHost() {
        return this.baseUri.getHost();
    }

    /**
     * @param path
     * @return
     * @throws URISyntaxException
     */
    public UriBuilder setPath(String path) throws URISyntaxException {
        this.baseUri =
            this.update(this.baseUri.getScheme(), this.baseUri.getUserInfo(), this.baseUri.getHost(),
                        this.baseUri.getPort(),
                        (new URI((String) null, (String) null, path, (String) null, (String) null)).getRawPath(),
                        this.baseUri.getRawQuery(), this.baseUri.getRawFragment());
        return this;
    }

    /**
     * @return
     */
    public String getPath() {
        return this.baseUri.getPath();
    }

    /**
     * @param nameValuePairs
     * @return
     * @throws URISyntaxException
     */
    protected UriBuilder setQueryNameValuePair(List<NameValuePair> nameValuePairs) throws URISyntaxException {
        StringBuilder uriBuilder = new StringBuilder();
        String path = this.baseUri.getRawPath();
        if (path != null) {
            uriBuilder.append(path);
        }

        uriBuilder.append('?');
        uriBuilder.append(URLEncodedUtils.format(nameValuePairs, "UTF-8"));
        String frag = this.baseUri.getRawFragment();
        if (frag != null) {
            uriBuilder.append('#').append(frag);
        }

        this.baseUri = this.baseUri.resolve(uriBuilder.toString());
        return this;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    protected final BasicNameValuePair toNameValuePair(final Object key, final Object value) {
        return new BasicNameValuePair(key.toString(), BeanUtils.toString(value));
    }

    /**
     * @param params
     * @return
     */
    protected final List<NameValuePair> toNameValuePairs(final Map<?, ?> params) {
        List<NameValuePair> nameValuePairs = null;
        if (params != null && params.size() > 0) {
            nameValuePairs = new ArrayList(params.size());
            final Iterator<?> itr = params.keySet().iterator();
            while (itr.hasNext()) {
                final Object key = itr.next();
                final Object value = params.get(key);
                if (value instanceof List) {
                    final Iterator itrInner = ((List) value).iterator();
                    while (itrInner.hasNext()) {
                        nameValuePairs.add(toNameValuePair(key, itrInner.next()));
                    }
                } else {
                    nameValuePairs.add(toNameValuePair(key, value));
                }
            }
        }

        return nameValuePairs;
    }

    /**
     * @param params
     * @return
     * @throws URISyntaxException
     */
    public UriBuilder setQuery(final Map<?, ?> params) throws URISyntaxException {
        if (params != null && params.size() >= 1) {
            final List<NameValuePair> nameValuePairList = new ArrayList(params.size());
            final Iterator itr = params.keySet().iterator();
            while (itr.hasNext()) {
                final Object key = itr.next();
                final Object value = params.get(key);
                if (value instanceof List) {
                    final Iterator itrInner = ((List) value).iterator();
                    while (itrInner.hasNext()) {
                        nameValuePairList.add(toNameValuePair(key, itrInner.next()));
                    }
                } else {
                    nameValuePairList.add(toNameValuePair(key, value));
                }
            }
            this.setQueryNameValuePair(nameValuePairList);
        } else {
            this.baseUri =
                new URI(this.baseUri.getScheme(), this.baseUri.getUserInfo(), this.baseUri.getHost(),
                        this.baseUri.getPort(),
                        this.baseUri.getPath(), (String) null, this.baseUri.getFragment());
        }

        return this;
    }

    /**
     * @param query
     * @return
     * @throws URISyntaxException
     */
    public UriBuilder setRawQuery(String query) throws URISyntaxException {
        this.baseUri =
            this.update(this.baseUri.getScheme(), this.baseUri.getUserInfo(), this.baseUri.getHost(),
                        this.baseUri.getPort(),
                        this.baseUri.getRawPath(), query, this.baseUri.getRawFragment());
        return this;
    }

    /**
     * @return
     */
    public Map<String, Object> getQuery() {
        Map<String, Object> queryParams = null;
        final List<NameValuePair> pairs = this.getQueryNameValuePair();
        if (pairs != null) {
            queryParams = new HashMap();
            final Iterator<NameValuePair> itr = pairs.iterator();
            while (itr.hasNext()) {
                final NameValuePair nameValuePair = itr.next();
                final String key = nameValuePair.getName();
                Object oldValue = queryParams.get(key);
                if (oldValue == null) {
                    queryParams.put(key, nameValuePair.getValue());
                } else if (oldValue instanceof List) {
                    ((List) oldValue).add(nameValuePair.getValue());
                } else {
                    final List<String> keyValueList = new ArrayList(2);
                    keyValueList.add((String) oldValue);
                    keyValueList.add(nameValuePair.getValue());
                    queryParams.put(key, keyValueList);
                }
            }
        }

        return queryParams;
    }


    /**
     * @return
     */
    protected List<NameValuePair> getQueryNameValuePair() {
        return (this.baseUri.getQuery() == null ? null : newList(URLEncodedUtils.parse(this.baseUri, UTF_8)));
    }

    /**
     * @param name
     * @return
     */
    public boolean hasQueryParam(final String name) {
        return this.getQuery().get(name) != null;
    }

    /**
     * @param param
     * @return
     * @throws URISyntaxException
     */
    public UriBuilder removeQueryParam(String param) throws URISyntaxException {
        final List<NameValuePair> params = this.getQueryNameValuePair();
        NameValuePair found = null;
        Iterator<NameValuePair> itr = params.iterator();
        while (itr.hasNext()) {
            NameValuePair nameValuePair = itr.next();
            if (nameValuePair.getName().equals(param)) {
                found = nameValuePair;
                break;
            }
        }

        if (found == null) {
            throw new IllegalArgumentException("Param '" + param + "' not found");
        } else {
            params.remove(found);
            this.setQueryNameValuePair(params);
            return this;
        }
    }

    /**
     * @param nameValuePair
     * @return
     * @throws URISyntaxException
     */
    protected UriBuilder addQueryParam(final NameValuePair nameValuePair) throws URISyntaxException {
        List<NameValuePair> params = this.getQueryNameValuePair();
        if (params == null) {
            params = new ArrayList<NameValuePair>();
        }
        params.add(nameValuePair);
        this.setQueryNameValuePair(params);
        return this;
    }

    /**
     * @param param
     * @param value
     * @return
     * @throws URISyntaxException
     */
    public UriBuilder addQueryParam(String param, Object value) throws URISyntaxException {
        this.addQueryParam(new BasicNameValuePair(param, value != null ? value.toString() : ""));
        return this;
    }

    /**
     * @param nameValuePairs
     * @return
     * @throws URISyntaxException
     */
    protected UriBuilder addQueryParams(List<NameValuePair> nameValuePairs) throws URISyntaxException {
        List<NameValuePair> params = this.getQueryNameValuePair();
        if (params == null) {
            params = new ArrayList<NameValuePair>();
        }
        params.addAll(nameValuePairs);
        this.setQueryNameValuePair(params);
        return this;
    }

    /**
     * @param params
     * @return
     * @throws URISyntaxException
     */
    public UriBuilder addQueryParams(Map<Object, ?> params) throws URISyntaxException {
        final List<NameValuePair> nameValuePairs = new ArrayList();
        final Iterator<?> itr = params.keySet().iterator();
        while (itr.hasNext()) {
            final Object key = itr.next();
            final Object value = params.get(key);
            if (value instanceof List) {
                final Iterator itrInner = ((List) value).iterator();
                while (itrInner.hasNext()) {
                    nameValuePairs.add(toNameValuePair(key, itrInner.next()));
                }
            } else {
                nameValuePairs.add(toNameValuePair(key, value));
            }
        }

        this.addQueryParams(nameValuePairs);
        return this;
    }

    public UriBuilder setFragment(String fragment) throws URISyntaxException {
        this.baseUri =
            this.update(this.baseUri.getScheme(), this.baseUri.getUserInfo(), this.baseUri.getHost(),
                        this.baseUri.getPort(),
                        this.baseUri.getRawPath(), this.baseUri.getRawQuery(),
                        (new URI((String) null, (String) null, (String) null, fragment)).getRawFragment());
        return this;
    }

    public String getFragment() {
        return this.baseUri.getFragment();
    }

    public UriBuilder setUserInfo(String userInfo) throws URISyntaxException {
        this.baseUri =
            this.update(this.baseUri.getScheme(), userInfo, this.baseUri.getHost(), this.baseUri.getPort(),
                        this.baseUri.getRawPath(), this.baseUri.getRawQuery(), this.baseUri.getRawFragment());
        return this;
    }

    public String getUserInfo() {
        return this.baseUri.getUserInfo();
    }

    public String toString() {
        return this.baseUri.toString();
    }

    public URL toURL() throws MalformedURLException {
        return this.baseUri.toURL();
    }

    public URI toURI() {
        return this.baseUri;
    }

    /**
     * @param type
     * @return
     * @throws MalformedURLException
     */
    public Object asType(Class<?> type) throws MalformedURLException {
        if (type == URI.class) {
            return this.toURI();
        } else if (type == URL.class) {
            return this.toURL();
        } else if (type == String.class) {
            return this.toString();
        } else {
            throw new ClassCastException("Cannot cast instance of UriBuilder to class " + type);
        }
    }

    protected UriBuilder clone() {
        return new UriBuilder(this.baseUri);
    }

    /**
     * @param object
     * @return
     */
    public boolean equals(Object object) {
        return !(object instanceof UriBuilder) ? false : this.baseUri.equals(((UriBuilder) object).toURI());
    }
}
