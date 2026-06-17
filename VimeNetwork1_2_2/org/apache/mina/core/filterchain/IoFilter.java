/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.filterchain;

import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public interface IoFilter {
    public void init() throws Exception;

    public void destroy() throws Exception;

    public void onPreAdd(IoFilterChain var1, String var2, NextFilter var3) throws Exception;

    public void onPostAdd(IoFilterChain var1, String var2, NextFilter var3) throws Exception;

    public void onPreRemove(IoFilterChain var1, String var2, NextFilter var3) throws Exception;

    public void onPostRemove(IoFilterChain var1, String var2, NextFilter var3) throws Exception;

    public void sessionCreated(NextFilter var1, IoSession var2) throws Exception;

    public void sessionOpened(NextFilter var1, IoSession var2) throws Exception;

    public void sessionClosed(NextFilter var1, IoSession var2) throws Exception;

    public void sessionIdle(NextFilter var1, IoSession var2, IdleStatus var3) throws Exception;

    public void exceptionCaught(NextFilter var1, IoSession var2, Throwable var3) throws Exception;

    public void inputClosed(NextFilter var1, IoSession var2) throws Exception;

    public void messageReceived(NextFilter var1, IoSession var2, Object var3) throws Exception;

    public void messageSent(NextFilter var1, IoSession var2, WriteRequest var3) throws Exception;

    public void filterClose(NextFilter var1, IoSession var2) throws Exception;

    public void filterWrite(NextFilter var1, IoSession var2, WriteRequest var3) throws Exception;

    public static interface NextFilter {
        public void sessionCreated(IoSession var1);

        public void sessionOpened(IoSession var1);

        public void sessionClosed(IoSession var1);

        public void sessionIdle(IoSession var1, IdleStatus var2);

        public void exceptionCaught(IoSession var1, Throwable var2);

        public void inputClosed(IoSession var1);

        public void messageReceived(IoSession var1, Object var2);

        public void messageSent(IoSession var1, WriteRequest var2);

        public void filterWrite(IoSession var1, WriteRequest var2);

        public void filterClose(IoSession var1);
    }
}

