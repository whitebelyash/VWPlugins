/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.proxy.handlers.http.digest;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.proxy.ProxyAuthException;
import org.apache.mina.proxy.handlers.http.AbstractAuthLogicHandler;
import org.apache.mina.proxy.handlers.http.HttpProxyRequest;
import org.apache.mina.proxy.handlers.http.HttpProxyResponse;
import org.apache.mina.proxy.handlers.http.digest.DigestUtilities;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.apache.mina.proxy.utils.StringUtilities;
import org.apache.mina.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpDigestAuthLogicHandler
extends AbstractAuthLogicHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpDigestAuthLogicHandler.class);
    private HashMap<String, String> directives = null;
    private HttpProxyResponse response;
    private static SecureRandom rnd;

    public HttpDigestAuthLogicHandler(ProxyIoSession proxyIoSession) throws ProxyAuthException {
        super(proxyIoSession);
        ((HttpProxyRequest)this.request).checkRequiredProperties("USER", "PWD");
    }

    @Override
    public void doHandshake(IoFilter.NextFilter nextFilter) throws ProxyAuthException {
        HashMap<String, List<String>> headers;
        logger.debug(" doHandshake()");
        if (this.step > 0 && this.directives == null) {
            throw new ProxyAuthException("Authentication challenge not received");
        }
        HttpProxyRequest req = (HttpProxyRequest)this.request;
        HashMap<String, List<String>> hashMap = headers = req.getHeaders() != null ? req.getHeaders() : new HashMap<String, List<String>>();
        if (this.step > 0) {
            logger.debug("  sending DIGEST challenge response");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("username", req.getProperties().get("USER"));
            StringUtilities.copyDirective(this.directives, map, "realm");
            StringUtilities.copyDirective(this.directives, map, "uri");
            StringUtilities.copyDirective(this.directives, map, "opaque");
            StringUtilities.copyDirective(this.directives, map, "nonce");
            String algorithm = StringUtilities.copyDirective(this.directives, map, "algorithm");
            if (algorithm != null && !"md5".equalsIgnoreCase(algorithm) && !"md5-sess".equalsIgnoreCase(algorithm)) {
                throw new ProxyAuthException("Unknown algorithm required by server");
            }
            String qop = this.directives.get("qop");
            if (qop != null) {
                StringTokenizer st = new StringTokenizer(qop, ",");
                String token = null;
                while (st.hasMoreTokens()) {
                    String tk = st.nextToken();
                    if ("auth".equalsIgnoreCase(token)) break;
                    int pos = Arrays.binarySearch(DigestUtilities.SUPPORTED_QOPS, tk);
                    if (pos <= -1) continue;
                    token = tk;
                }
                if (token != null) {
                    map.put("qop", token);
                    byte[] nonce = new byte[8];
                    rnd.nextBytes(nonce);
                    try {
                        String cnonce = new String(Base64.encodeBase64(nonce), this.proxyIoSession.getCharsetName());
                        map.put("cnonce", cnonce);
                    }
                    catch (UnsupportedEncodingException e) {
                        throw new ProxyAuthException("Unable to encode cnonce", e);
                    }
                } else {
                    throw new ProxyAuthException("No supported qop option available");
                }
            }
            map.put("nc", "00000001");
            map.put("uri", req.getHttpURI());
            try {
                map.put("response", DigestUtilities.computeResponseValue(this.proxyIoSession.getSession(), map, req.getHttpVerb().toUpperCase(), req.getProperties().get("PWD"), this.proxyIoSession.getCharsetName(), this.response.getBody()));
            }
            catch (Exception e) {
                throw new ProxyAuthException("Digest response computing failed", e);
            }
            StringBuilder sb = new StringBuilder("Digest ");
            boolean addSeparator = false;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                if (addSeparator) {
                    sb.append(", ");
                } else {
                    addSeparator = true;
                }
                boolean quotedValue = !"qop".equals(key) && !"nc".equals(key);
                sb.append(key);
                if (quotedValue) {
                    sb.append("=\"").append(entry.getValue()).append('\"');
                    continue;
                }
                sb.append('=').append(entry.getValue());
            }
            StringUtilities.addValueToHeader(headers, "Proxy-Authorization", sb.toString(), true);
        }
        HttpDigestAuthLogicHandler.addKeepAliveHeaders(headers);
        req.setHeaders(headers);
        this.writeRequest(nextFilter, req);
        ++this.step;
    }

    @Override
    public void handleResponse(HttpProxyResponse response) throws ProxyAuthException {
        this.response = response;
        if (this.step == 0) {
            if (response.getStatusCode() != 401 && response.getStatusCode() != 407) {
                throw new ProxyAuthException("Received unexpected response code (" + response.getStatusLine() + ").");
            }
            List<String> values = response.getHeaders().get("Proxy-Authenticate");
            String challengeResponse = null;
            for (String s : values) {
                if (!s.startsWith("Digest")) continue;
                challengeResponse = s;
                break;
            }
            if (challengeResponse == null) {
                throw new ProxyAuthException("Server doesn't support digest authentication method !");
            }
            try {
                this.directives = StringUtilities.parseDirectives(challengeResponse.substring(7).getBytes(this.proxyIoSession.getCharsetName()));
            }
            catch (Exception e) {
                throw new ProxyAuthException("Parsing of server digest directives failed", e);
            }
        } else {
            throw new ProxyAuthException("Received unexpected response code (" + response.getStatusLine() + ").");
        }
        this.step = 1;
    }

    static {
        try {
            rnd = SecureRandom.getInstance("SHA1PRNG");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

