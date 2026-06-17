/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.filterchain;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public class IoFilterAdapter
implements IoFilter {
    @Override
    public void init() throws Exception {
    }

    @Override
    public void destroy() throws Exception {
    }

    @Override
    public void onPreAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter) throws Exception {
    }

    @Override
    public void onPostAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter) throws Exception {
    }

    @Override
    public void onPreRemove(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter) throws Exception {
    }

    @Override
    public void onPostRemove(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter) throws Exception {
    }

    @Override
    public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        nextFilter.sessionCreated(session);
    }

    @Override
    public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        nextFilter.sessionOpened(session);
    }

    @Override
    public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        nextFilter.sessionClosed(session);
    }

    @Override
    public void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
        nextFilter.sessionIdle(session, status);
    }

    @Override
    public void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
        nextFilter.exceptionCaught(session, cause);
    }

    @Override
    public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message) throws Exception {
        nextFilter.messageReceived(session, message);
    }

    @Override
    public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        nextFilter.messageSent(session, writeRequest);
    }

    @Override
    public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        nextFilter.filterWrite(session, writeRequest);
    }

    @Override
    public void filterClose(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        nextFilter.filterClose(session);
    }

    @Override
    public void inputClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        nextFilter.inputClosed(session);
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }
}

