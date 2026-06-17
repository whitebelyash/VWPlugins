/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.proxy.handlers.http;

import java.util.HashMap;
import java.util.List;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.proxy.ProxyAuthException;
import org.apache.mina.proxy.handlers.http.AbstractAuthLogicHandler;
import org.apache.mina.proxy.handlers.http.AbstractHttpLogicHandler;
import org.apache.mina.proxy.handlers.http.HttpAuthenticationMethods;
import org.apache.mina.proxy.handlers.http.HttpProxyRequest;
import org.apache.mina.proxy.handlers.http.HttpProxyResponse;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.apache.mina.proxy.utils.StringUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSmartProxyHandler
extends AbstractHttpLogicHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpSmartProxyHandler.class);
    private boolean requestSent = false;
    private AbstractAuthLogicHandler authHandler;

    public HttpSmartProxyHandler(ProxyIoSession proxyIoSession) {
        super(proxyIoSession);
    }

    @Override
    public void doHandshake(IoFilter.NextFilter nextFilter) throws ProxyAuthException {
        logger.debug(" doHandshake()");
        if (this.authHandler != null) {
            this.authHandler.doHandshake(nextFilter);
        } else {
            if (this.requestSent) {
                throw new ProxyAuthException("Authentication request already sent");
            }
            logger.debug("  sending HTTP request");
            HttpProxyRequest req = (HttpProxyRequest)this.getProxyIoSession().getRequest();
            HashMap<String, List<String>> headers = req.getHeaders() != null ? req.getHeaders() : new HashMap<String, List<String>>();
            AbstractAuthLogicHandler.addKeepAliveHeaders(headers);
            req.setHeaders(headers);
            this.writeRequest(nextFilter, req);
            this.requestSent = true;
        }
    }

    private void autoSelectAuthHandler(HttpProxyResponse response) throws ProxyAuthException {
        List<String> values = response.getHeaders().get("Proxy-Authenticate");
        ProxyIoSession proxyIoSession = this.getProxyIoSession();
        if (values == null || values.size() == 0) {
            this.authHandler = HttpAuthenticationMethods.NO_AUTH.getNewHandler(proxyIoSession);
        } else if (this.getProxyIoSession().getPreferedOrder() == null) {
            int method = -1;
            for (String proxyAuthHeader : values) {
                if ((proxyAuthHeader = proxyAuthHeader.toLowerCase()).contains("ntlm")) {
                    method = HttpAuthenticationMethods.NTLM.getId();
                    break;
                }
                if (proxyAuthHeader.contains("digest") && method != HttpAuthenticationMethods.NTLM.getId()) {
                    method = HttpAuthenticationMethods.DIGEST.getId();
                    continue;
                }
                if (!proxyAuthHeader.contains("basic") || method != -1) continue;
                method = HttpAuthenticationMethods.BASIC.getId();
            }
            if (method != -1) {
                try {
                    this.authHandler = HttpAuthenticationMethods.getNewHandler(method, proxyIoSession);
                }
                catch (Exception ex) {
                    logger.debug("Following exception occured:", ex);
                }
            }
            if (this.authHandler == null) {
                this.authHandler = HttpAuthenticationMethods.NO_AUTH.getNewHandler(proxyIoSession);
            }
        } else {
            block5: for (HttpAuthenticationMethods method : proxyIoSession.getPreferedOrder()) {
                if (this.authHandler != null) break;
                if (method == HttpAuthenticationMethods.NO_AUTH) {
                    this.authHandler = HttpAuthenticationMethods.NO_AUTH.getNewHandler(proxyIoSession);
                    break;
                }
                for (String proxyAuthHeader : values) {
                    proxyAuthHeader = proxyAuthHeader.toLowerCase();
                    try {
                        if (proxyAuthHeader.contains("basic") && method == HttpAuthenticationMethods.BASIC) {
                            this.authHandler = HttpAuthenticationMethods.BASIC.getNewHandler(proxyIoSession);
                            continue block5;
                        }
                        if (proxyAuthHeader.contains("digest") && method == HttpAuthenticationMethods.DIGEST) {
                            this.authHandler = HttpAuthenticationMethods.DIGEST.getNewHandler(proxyIoSession);
                            continue block5;
                        }
                        if (!proxyAuthHeader.contains("ntlm") || method != HttpAuthenticationMethods.NTLM) continue;
                        this.authHandler = HttpAuthenticationMethods.NTLM.getNewHandler(proxyIoSession);
                        continue block5;
                    }
                    catch (Exception ex) {
                        logger.debug("Following exception occured:", ex);
                    }
                }
            }
        }
        if (this.authHandler == null) {
            throw new ProxyAuthException("Unknown authentication mechanism(s): " + values);
        }
    }

    @Override
    public void handleResponse(HttpProxyResponse response) throws ProxyAuthException {
        if (!this.isHandshakeComplete() && ("close".equalsIgnoreCase(StringUtilities.getSingleValuedHeader(response.getHeaders(), "Proxy-Connection")) || "close".equalsIgnoreCase(StringUtilities.getSingleValuedHeader(response.getHeaders(), "Connection")))) {
            this.getProxyIoSession().setReconnectionNeeded(true);
        }
        if (response.getStatusCode() == 407) {
            if (this.authHandler == null) {
                this.autoSelectAuthHandler(response);
            }
        } else {
            throw new ProxyAuthException("Error: unexpected response code " + response.getStatusLine() + " received from proxy.");
        }
        this.authHandler.handleResponse(response);
    }
}

