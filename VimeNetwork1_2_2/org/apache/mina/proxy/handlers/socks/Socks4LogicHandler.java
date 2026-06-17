/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.proxy.handlers.socks;

import java.util.Arrays;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.proxy.handlers.socks.AbstractSocksLogicHandler;
import org.apache.mina.proxy.handlers.socks.SocksProxyConstants;
import org.apache.mina.proxy.handlers.socks.SocksProxyRequest;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.apache.mina.proxy.utils.ByteUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Socks4LogicHandler
extends AbstractSocksLogicHandler {
    private static final Logger logger = LoggerFactory.getLogger(Socks4LogicHandler.class);

    public Socks4LogicHandler(ProxyIoSession proxyIoSession) {
        super(proxyIoSession);
    }

    @Override
    public void doHandshake(IoFilter.NextFilter nextFilter) {
        logger.debug(" doHandshake()");
        this.writeRequest(nextFilter, this.request);
    }

    protected void writeRequest(IoFilter.NextFilter nextFilter, SocksProxyRequest request) {
        try {
            boolean isV4ARequest = Arrays.equals(request.getIpAddress(), SocksProxyConstants.FAKE_IP);
            byte[] userID = request.getUserName().getBytes("ASCII");
            byte[] host = isV4ARequest ? request.getHost().getBytes("ASCII") : null;
            int len = 9 + userID.length;
            if (isV4ARequest) {
                len += host.length + 1;
            }
            IoBuffer buf = IoBuffer.allocate(len);
            buf.put(request.getProtocolVersion());
            buf.put(request.getCommandCode());
            buf.put(request.getPort());
            buf.put(request.getIpAddress());
            buf.put(userID);
            buf.put((byte)0);
            if (isV4ARequest) {
                buf.put(host);
                buf.put((byte)0);
            }
            if (isV4ARequest) {
                logger.debug("  sending SOCKS4a request");
            } else {
                logger.debug("  sending SOCKS4 request");
            }
            buf.flip();
            this.writeData(nextFilter, buf);
        }
        catch (Exception ex) {
            this.closeSession("Unable to send Socks request: ", ex);
        }
    }

    @Override
    public void messageReceived(IoFilter.NextFilter nextFilter, IoBuffer buf) {
        try {
            if (buf.remaining() >= 8) {
                this.handleResponse(buf);
            }
        }
        catch (Exception ex) {
            this.closeSession("Proxy handshake failed: ", ex);
        }
    }

    protected void handleResponse(IoBuffer buf) throws Exception {
        byte first = buf.get(0);
        if (first != 0) {
            throw new Exception("Socks response seems to be malformed");
        }
        byte status = buf.get(1);
        buf.position(buf.position() + 8);
        if (status != 90) {
            throw new Exception("Proxy handshake failed - Code: 0x" + ByteUtilities.asHex(new byte[]{status}) + " (" + SocksProxyConstants.getReplyCodeAsString(status) + ")");
        }
        this.setHandshakeComplete();
    }
}

