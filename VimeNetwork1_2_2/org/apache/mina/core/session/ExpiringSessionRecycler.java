/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.session;

import java.net.SocketAddress;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionRecycler;
import org.apache.mina.util.ExpirationListener;
import org.apache.mina.util.ExpiringMap;

public class ExpiringSessionRecycler
implements IoSessionRecycler {
    private ExpiringMap<SocketAddress, IoSession> sessionMap;
    private ExpiringMap.Expirer mapExpirer;

    public ExpiringSessionRecycler() {
        this(60);
    }

    public ExpiringSessionRecycler(int timeToLive) {
        this(timeToLive, 1);
    }

    public ExpiringSessionRecycler(int timeToLive, int expirationInterval) {
        this.sessionMap = new ExpiringMap(timeToLive, expirationInterval);
        this.mapExpirer = this.sessionMap.getExpirer();
        this.sessionMap.addExpirationListener(new DefaultExpirationListener());
    }

    @Override
    public void put(IoSession session) {
        this.mapExpirer.startExpiringIfNotStarted();
        SocketAddress key = session.getRemoteAddress();
        if (!this.sessionMap.containsKey(key)) {
            this.sessionMap.put(key, session);
        }
    }

    @Override
    public IoSession recycle(SocketAddress remoteAddress) {
        return this.sessionMap.get(remoteAddress);
    }

    @Override
    public void remove(IoSession session) {
        this.sessionMap.remove(session.getRemoteAddress());
    }

    public void stopExpiring() {
        this.mapExpirer.stopExpiring();
    }

    public int getExpirationInterval() {
        return this.sessionMap.getExpirationInterval();
    }

    public int getTimeToLive() {
        return this.sessionMap.getTimeToLive();
    }

    public void setExpirationInterval(int expirationInterval) {
        this.sessionMap.setExpirationInterval(expirationInterval);
    }

    public void setTimeToLive(int timeToLive) {
        this.sessionMap.setTimeToLive(timeToLive);
    }

    private class DefaultExpirationListener
    implements ExpirationListener<IoSession> {
        private DefaultExpirationListener() {
        }

        @Override
        public void expired(IoSession expiredSession) {
            expiredSession.closeNow();
        }
    }
}

