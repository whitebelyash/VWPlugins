/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.proxy.handlers.http;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.apache.mina.proxy.ProxyAuthException;
import org.apache.mina.proxy.handlers.ProxyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProxyRequest
extends ProxyRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpProxyRequest.class);
    private final String httpVerb;
    private final String httpURI;
    private String httpVersion;
    private String host;
    private Map<String, List<String>> headers;
    private transient Map<String, String> properties;

    public HttpProxyRequest(InetSocketAddress endpointAddress) {
        this(endpointAddress, "HTTP/1.0", null);
    }

    public HttpProxyRequest(InetSocketAddress endpointAddress, String httpVersion) {
        this(endpointAddress, httpVersion, null);
    }

    public HttpProxyRequest(InetSocketAddress endpointAddress, String httpVersion, Map<String, List<String>> headers) {
        this.httpVerb = "CONNECT";
        this.httpURI = !endpointAddress.isUnresolved() ? endpointAddress.getHostName() + ":" + endpointAddress.getPort() : endpointAddress.getAddress().getHostAddress() + ":" + endpointAddress.getPort();
        this.httpVersion = httpVersion;
        this.headers = headers;
    }

    public HttpProxyRequest(String httpURI) {
        this("GET", httpURI, "HTTP/1.0", null);
    }

    public HttpProxyRequest(String httpURI, String httpVersion) {
        this("GET", httpURI, httpVersion, null);
    }

    public HttpProxyRequest(String httpVerb, String httpURI, String httpVersion) {
        this(httpVerb, httpURI, httpVersion, null);
    }

    public HttpProxyRequest(String httpVerb, String httpURI, String httpVersion, Map<String, List<String>> headers) {
        this.httpVerb = httpVerb;
        this.httpURI = httpURI;
        this.httpVersion = httpVersion;
        this.headers = headers;
    }

    public final String getHttpVerb() {
        return this.httpVerb;
    }

    public String getHttpVersion() {
        return this.httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public final synchronized String getHost() {
        if (this.host == null) {
            if (this.getEndpointAddress() != null && !this.getEndpointAddress().isUnresolved()) {
                this.host = this.getEndpointAddress().getHostName();
            }
            if (this.host == null && this.httpURI != null) {
                try {
                    this.host = new URL(this.httpURI).getHost();
                }
                catch (MalformedURLException e) {
                    logger.debug("Malformed URL", e);
                }
            }
        }
        return this.host;
    }

    public final String getHttpURI() {
        return this.httpURI;
    }

    public final Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    public final void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public void checkRequiredProperties(String ... propNames) throws ProxyAuthException {
        StringBuilder sb = new StringBuilder();
        for (String propertyName : propNames) {
            if (this.properties.get(propertyName) != null) continue;
            sb.append(propertyName).append(' ');
        }
        if (sb.length() > 0) {
            sb.append("property(ies) missing in request");
            throw new ProxyAuthException(sb.toString());
        }
    }

    public String toHttpString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getHttpVerb()).append(' ').append(this.getHttpURI()).append(' ').append(this.getHttpVersion()).append("\r\n");
        boolean hostHeaderFound = false;
        if (this.getHeaders() != null) {
            for (Map.Entry<String, List<String>> header : this.getHeaders().entrySet()) {
                if (!hostHeaderFound) {
                    hostHeaderFound = header.getKey().equalsIgnoreCase("host");
                }
                for (String value : header.getValue()) {
                    sb.append(header.getKey()).append(": ").append(value).append("\r\n");
                }
            }
            if (!hostHeaderFound && this.getHttpVersion() == "HTTP/1.1") {
                sb.append("Host: ").append(this.getHost()).append("\r\n");
            }
        }
        sb.append("\r\n");
        return sb.toString();
    }
}

