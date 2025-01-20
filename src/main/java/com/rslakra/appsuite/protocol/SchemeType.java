package com.rslakra.appsuite.protocol;

/**
 * A protocol scheme is a combination of a protocol and a scheme, where a
 * protocol is how a browser communicates with a service, and a scheme is how a
 * browser handles a URL.
 * <p>
 * Explanation:
 * - Protocol: The way a browser communicates with a service
 * - Scheme: The way a browser handles a URL
 * <p>
 * The scheme is the first part of a Uniform Resource Identifier (URI) before
 * the colon character. It tells the browser which protocol to use to get a
 * resource.
 * <p>
 * Examples of protocols and schemes:
 * - HTTP: Hypertext Transfer Protocol, a common protocol used in URLs
 * - HTTPS: Hypertext Transfer Protocol Secure, a common protocol used in URLs
 * - FTP: File Transfer Protocol, a common protocol used in URLs
 * - Blob: Binary Large Object, a scheme that points to a large in-memory object
 * - Data: A scheme that embeds data directly in the URL
 * - File: A scheme that uses host-specific file names
 * - JavaScript: A scheme that embeds JavaScript code in the URL
 * - Mailto: A scheme that uses an electronic mail address
 * - LDAP: The Lightweight Directory Access Protocol, which is used to access
 * resources through LDAP
 */

public enum SchemeType {
    /* Hypertext Transfer Protocol, a common protocol used in URLs */
    HTTP,
    /* Hypertext Transfer Protocol Secure, a common protocol used in URLs */
    HTTPS,
    /* File Transfer Protocol, a common protocol used in URLs */
    FTP,
    TCP,
    UDP,
}

