/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.util;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilterEvent;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public abstract class CommonEventFilter
extends IoFilterAdapter {
    protected abstract void filter(IoFilterEvent var1) throws Exception;

    @Override
    public final void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        this.filter(new IoFilterEvent(nextFilter, IoEventType.SESSION_CREATED, session, null));
    }

    @Override
    public final void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        this.filter(new IoFilterEvent(nextFilter, IoEventType.SESSION_OPENED, session, null));
    }

    @Override
    public final void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        this.filter(new IoFilterEvent(nextFilter, IoEventType.SESSION_CLOSED, session, null));
    }

    @Override
    public final void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
        this.filter(new IoFilterEvent(nextFilter, IoEventType.SESSION_IDLE, session, status));
    }

    @Override
    public final void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
        this.filter(new IoFilterEvent(nextFilter, IoEventType.EXCEPTION_CAUGHT, session, cause));
    }

    @Override
    public final void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message) throws Exception {
        this.filter(new IoFilterEvent(nextFilter, IoEventType.MESSAGE_RECEIVED, session, message));
    }

    @Override
    public final void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        this.filter(new IoFilterEvent(nextFilter, IoEventType.MESSAGE_SENT, session, writeRequest));
    }

    @Override
    public final void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        this.filter(new IoFilterEvent(nextFilter, IoEventType.WRITE, session, writeRequest));
    }

    @Override
    public final void filterClose(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        this.filter(new IoFilterEvent(nextFilter, IoEventType.CLOSE, session, null));
    }
}

