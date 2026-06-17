/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.connector;

import java.util.logging.Level;
import net.xtrafrancyz.Core.network.connector.CallbackData;
import net.xtrafrancyz.Core.network.connector.CoreConnector;

class WatcherThread
extends Thread {
    private static final int RECONNECT_DELAY_MILLIS = 10000;
    private final CoreConnector connector;

    public WatcherThread(CoreConnector connector) {
        super("Core watcher");
        this.connector = connector;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            long time = System.currentTimeMillis();
            if (this.connector.connectFuture == null) {
                if (!this.connector.isConnected() && time - this.connector.lastConnectAttempt > 10000L) {
                    this.connector.connect();
                } else if (this.connector.isConnected() && time - this.connector.connector.getStatistics().getLastIoTime() > 30000L) {
                    this.connector.logger.info("[Core] Last 30 seconds no one packet has been received");
                    this.connector.disconnect();
                    this.connector.connect();
                }
            }
            for (CallbackData data : this.connector.callbacks.values()) {
                if (data.timeToLive >= time) continue;
                this.connector.callbacks.remove(data.id);
                if (data.onTimeout == null) continue;
                try {
                    data.onTimeout.run();
                }
                catch (Exception e) {
                    this.connector.logger.log(Level.WARNING, null, e);
                }
            }
            try {
                Thread.sleep(50L);
            }
            catch (InterruptedException e) {
                break;
            }
        }
    }
}

