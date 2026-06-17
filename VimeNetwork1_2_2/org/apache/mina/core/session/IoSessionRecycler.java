/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.session;

import java.net.SocketAddress;
import org.apache.mina.core.session.IoSession;

public interface IoSessionRecycler {
    public static final IoSessionRecycler NOOP = new IoSessionRecycler(){

        @Override
        public void put(IoSession session) {
        }

        @Override
        public IoSession recycle(SocketAddress remoteAddress) {
            return null;
        }

        @Override
        public void remove(IoSession session) {
        }
    };

    public void put(IoSession var1);

    public IoSession recycle(SocketAddress var1);

    public void remove(IoSession var1);
}

