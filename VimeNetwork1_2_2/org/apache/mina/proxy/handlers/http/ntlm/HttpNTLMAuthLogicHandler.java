/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.proxy.handlers.http.ntlm;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.proxy.ProxyAuthException;
import org.apache.mina.proxy.handlers.http.AbstractAuthLogicHandler;
import org.apache.mina.proxy.handlers.http.HttpProxyRequest;
import org.apache.mina.proxy.handlers.http.HttpProxyResponse;
import org.apache.mina.proxy.handlers.http.ntlm.NTLMUtilities;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.apache.mina.proxy.utils.StringUtilities;
import org.apache.mina.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpNTLMAuthLogicHandler
extends AbstractAuthLogicHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpNTLMAuthLogicHandler.class);
    private byte[] challengePacket = null;

    public HttpNTLMAuthLogicHandler(ProxyIoSession proxyIoSession) throws ProxyAuthException {
        super(proxyIoSession);
        ((HttpProxyRequest)this.request).checkRequiredProperties("USER", "PWD", "DOMAIN", "WORKSTATION");
    }

    @Override
    public void doHandshake(IoFilter.NextFilter nextFilter) throws ProxyAuthException {
        LOGGER.debug(" doHandshake()");
        if (this.step > 0 && this.challengePacket == null) {
            throw new IllegalStateException("NTLM Challenge packet not received");
        }
        HttpProxyRequest req = (HttpProxyRequest)this.request;
        HashMap<String, List<String>> headers = req.getHeaders() != null ? req.getHeaders() : new HashMap<String, List<String>>();
        String domain = req.getProperties().get("DOMAIN");
        String workstation = req.getProperties().get("WORKSTATION");
        if (this.step > 0) {
            LOGGER.debug("  sending NTLM challenge response");
            byte[] challenge = NTLMUtilities.extractChallengeFromType2Message(this.challengePacket);
            int serverFlags = NTLMUtilities.extractFlagsFromType2Message(this.challengePacket);
            String username = req.getProperties().get("USER");
            String password = req.getProperties().get("PWD");
            byte[] authenticationPacket = NTLMUtilities.createType3Message(username, password, challenge, domain, workstation, serverFlags, null);
            StringUtilities.addValueToHeader(headers, "Proxy-Authorization", "NTLM " + new String(Base64.encodeBase64(authenticationPacket)), true);
        } else {
            LOGGER.debug("  sending NTLM negotiation packet");
            byte[] negotiationPacket = NTLMUtilities.createType1Message(workstation, domain, null, null);
            StringUtilities.addValueToHeader(headers, "Proxy-Authorization", "NTLM " + new String(Base64.encodeBase64(negotiationPacket)), true);
        }
        HttpNTLMAuthLogicHandler.addKeepAliveHeaders(headers);
        req.setHeaders(headers);
        this.writeRequest(nextFilter, req);
        ++this.step;
    }

    private String getNTLMHeader(HttpProxyResponse response) {
        List<String> values = response.getHeaders().get("Proxy-Authenticate");
        for (String s : values) {
            if (!s.startsWith("NTLM")) continue;
            return s;
        }
        return null;
    }

    @Override
    public void handleResponse(HttpProxyResponse response) throws ProxyAuthException {
        String challengeResponse;
        if (this.step == 0) {
            challengeResponse = this.getNTLMHeader(response);
            this.step = 1;
            if (challengeResponse == null || challengeResponse.length() < 5) {
                return;
            }
        }
        if (this.step == 1) {
            challengeResponse = this.getNTLMHeader(response);
            if (challengeResponse == null || challengeResponse.length() < 5) {
                throw new ProxyAuthException("Unexpected error while reading server challenge !");
            }
            try {
                this.challengePacket = Base64.decodeBase64(challengeResponse.substring(5).getBytes(this.proxyIoSession.getCharsetName()));
            }
            catch (IOException e) {
                throw new ProxyAuthException("Unable to decode the base64 encoded NTLM challenge", e);
            }
        } else {
            throw new ProxyAuthException("Received unexpected response code (" + response.getStatusLine() + ").");
        }
        this.step = 2;
    }
}

