/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.proxy.event;

import java.util.LinkedList;
import java.util.Queue;
import org.apache.mina.proxy.event.IoSessionEvent;
import org.apache.mina.proxy.event.IoSessionEventType;
import org.apache.mina.proxy.handlers.socks.SocksProxyRequest;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IoSessionEventQueue {
    private static final Logger logger = LoggerFactory.getLogger(IoSessionEventQueue.class);
    private ProxyIoSession proxyIoSession;
    private Queue<IoSessionEvent> sessionEventsQueue = new LinkedList<IoSessionEvent>();

    public IoSessionEventQueue(ProxyIoSession proxyIoSession) {
        this.proxyIoSession = proxyIoSession;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void discardSessionQueueEvents() {
        Queue<IoSessionEvent> queue = this.sessionEventsQueue;
        synchronized (queue) {
            this.sessionEventsQueue.clear();
            logger.debug("Event queue CLEARED");
        }
    }

    public void enqueueEventIfNecessary(IoSessionEvent evt) {
        logger.debug("??? >> Enqueue {}", (Object)evt);
        if (this.proxyIoSession.getRequest() instanceof SocksProxyRequest) {
            evt.deliverEvent();
            return;
        }
        if (this.proxyIoSession.getHandler().isHandshakeComplete()) {
            evt.deliverEvent();
        } else if (evt.getType() == IoSessionEventType.CLOSED) {
            if (this.proxyIoSession.isAuthenticationFailed()) {
                this.proxyIoSession.getConnector().cancelConnectFuture();
                this.discardSessionQueueEvents();
                evt.deliverEvent();
            } else {
                this.discardSessionQueueEvents();
            }
        } else if (evt.getType() == IoSessionEventType.OPENED) {
            this.enqueueSessionEvent(evt);
            evt.deliverEvent();
        } else {
            this.enqueueSessionEvent(evt);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void flushPendingSessionEvents() throws Exception {
        Queue<IoSessionEvent> queue = this.sessionEventsQueue;
        synchronized (queue) {
            IoSessionEvent evt;
            while ((evt = this.sessionEventsQueue.poll()) != null) {
                logger.debug(" Flushing buffered event: {}", (Object)evt);
                evt.deliverEvent();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void enqueueSessionEvent(IoSessionEvent evt) {
        Queue<IoSessionEvent> queue = this.sessionEventsQueue;
        synchronized (queue) {
            logger.debug("Enqueuing event: {}", (Object)evt);
            this.sessionEventsQueue.offer(evt);
        }
    }
}

