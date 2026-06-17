/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.proxy.filter;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.proxy.ProxyAuthException;
import org.apache.mina.proxy.ProxyLogicHandler;
import org.apache.mina.proxy.event.IoSessionEvent;
import org.apache.mina.proxy.event.IoSessionEventType;
import org.apache.mina.proxy.filter.ProxyHandshakeIoBuffer;
import org.apache.mina.proxy.handlers.ProxyRequest;
import org.apache.mina.proxy.handlers.http.HttpSmartProxyHandler;
import org.apache.mina.proxy.handlers.socks.Socks4LogicHandler;
import org.apache.mina.proxy.handlers.socks.Socks5LogicHandler;
import org.apache.mina.proxy.handlers.socks.SocksProxyRequest;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyFilter
extends IoFilterAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyFilter.class);

    @Override
    public void onPreAdd(IoFilterChain chain, String name, IoFilter.NextFilter nextFilter) {
        if (chain.contains(ProxyFilter.class)) {
            throw new IllegalStateException("A filter chain cannot contain more than one ProxyFilter.");
        }
    }

    @Override
    public void onPreRemove(IoFilterChain chain, String name, IoFilter.NextFilter nextFilter) {
        IoSession session = chain.getSession();
        session.removeAttribute(ProxyIoSession.PROXY_SESSION);
    }

    @Override
    public void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
        ProxyIoSession proxyIoSession = (ProxyIoSession)session.getAttribute(ProxyIoSession.PROXY_SESSION);
        proxyIoSession.setAuthenticationFailed(true);
        super.exceptionCaught(nextFilter, session, cause);
    }

    private ProxyLogicHandler getProxyHandler(IoSession session) {
        ProxyLogicHandler handler = ((ProxyIoSession)session.getAttribute(ProxyIoSession.PROXY_SESSION)).getHandler();
        if (handler == null) {
            throw new IllegalStateException();
        }
        if (handler.getProxyIoSession().getProxyFilter() != this) {
            throw new IllegalArgumentException("Not managed by this filter.");
        }
        return handler;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message) throws ProxyAuthException {
        ProxyLogicHandler handler;
        ProxyLogicHandler proxyLogicHandler = handler = this.getProxyHandler(session);
        synchronized (proxyLogicHandler) {
            IoBuffer buf = (IoBuffer)message;
            if (handler.isHandshakeComplete()) {
                nextFilter.messageReceived(session, buf);
            } else {
                LOGGER.debug(" Data Read: {} ({})", (Object)handler, (Object)buf);
                while (buf.hasRemaining() && !handler.isHandshakeComplete()) {
                    LOGGER.debug(" Pre-handshake - passing to handler");
                    int pos = buf.position();
                    handler.messageReceived(nextFilter, buf);
                    if (buf.position() != pos && !session.isClosing()) continue;
                    return;
                }
                if (buf.hasRemaining()) {
                    LOGGER.debug(" Passing remaining data to next filter");
                    nextFilter.messageReceived(session, buf);
                }
            }
        }
    }

    @Override
    public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) {
        this.writeData(nextFilter, session, writeRequest, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeData(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest, boolean isHandshakeData) {
        ProxyLogicHandler handler;
        ProxyLogicHandler proxyLogicHandler = handler = this.getProxyHandler(session);
        synchronized (proxyLogicHandler) {
            if (handler.isHandshakeComplete()) {
                nextFilter.filterWrite(session, writeRequest);
            } else if (isHandshakeData) {
                LOGGER.debug("   handshake data: {}", writeRequest.getMessage());
                nextFilter.filterWrite(session, writeRequest);
            } else if (!session.isConnected()) {
                LOGGER.debug(" Write request on closed session. Request ignored.");
            } else {
                LOGGER.debug(" Handshaking is not complete yet. Buffering write request.");
                handler.enqueueWriteRequest(nextFilter, writeRequest);
            }
        }
    }

    @Override
    public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        if (writeRequest.getMessage() != null && writeRequest.getMessage() instanceof ProxyHandshakeIoBuffer) {
            return;
        }
        nextFilter.messageSent(session, writeRequest);
    }

    @Override
    public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        LOGGER.debug("Session created: " + session);
        ProxyIoSession proxyIoSession = (ProxyIoSession)session.getAttribute(ProxyIoSession.PROXY_SESSION);
        LOGGER.debug("  get proxyIoSession: " + proxyIoSession);
        proxyIoSession.setProxyFilter(this);
        ProxyLogicHandler handler = proxyIoSession.getHandler();
        if (handler == null) {
            SocksProxyRequest req;
            ProxyRequest request = proxyIoSession.getRequest();
            handler = request instanceof SocksProxyRequest ? ((req = (SocksProxyRequest)request).getProtocolVersion() == 4 ? new Socks4LogicHandler(proxyIoSession) : new Socks5LogicHandler(proxyIoSession)) : new HttpSmartProxyHandler(proxyIoSession);
            proxyIoSession.setHandler(handler);
            handler.doHandshake(nextFilter);
        }
        proxyIoSession.getEventQueue().enqueueEventIfNecessary(new IoSessionEvent(nextFilter, session, IoSessionEventType.CREATED));
    }

    @Override
    public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        ProxyIoSession proxyIoSession = (ProxyIoSession)session.getAttribute(ProxyIoSession.PROXY_SESSION);
        proxyIoSession.getEventQueue().enqueueEventIfNecessary(new IoSessionEvent(nextFilter, session, IoSessionEventType.OPENED));
    }

    @Override
    public void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
        ProxyIoSession proxyIoSession = (ProxyIoSession)session.getAttribute(ProxyIoSession.PROXY_SESSION);
        proxyIoSession.getEventQueue().enqueueEventIfNecessary(new IoSessionEvent(nextFilter, session, status));
    }

    @Override
    public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        ProxyIoSession proxyIoSession = (ProxyIoSession)session.getAttribute(ProxyIoSession.PROXY_SESSION);
        proxyIoSession.getEventQueue().enqueueEventIfNecessary(new IoSessionEvent(nextFilter, session, IoSessionEventType.CLOSED));
    }
}

