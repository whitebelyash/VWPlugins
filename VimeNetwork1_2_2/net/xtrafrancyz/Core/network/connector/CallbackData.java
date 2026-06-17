/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.connector;

import net.xtrafrancyz.Core.network.connector.CoreCallback;

class CallbackData {
    public int id;
    public CoreCallback callback;
    public Runnable onTimeout;
    public long timeToLive;

    public CallbackData(int id, CoreCallback callback, long timeout, Runnable onTimeout) {
        this.id = id;
        this.callback = callback;
        this.timeToLive = System.currentTimeMillis() + timeout;
        this.onTimeout = onTimeout;
    }
}

