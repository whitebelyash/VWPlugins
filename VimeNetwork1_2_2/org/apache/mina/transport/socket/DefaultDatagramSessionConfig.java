/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.socket;

import org.apache.mina.transport.socket.AbstractDatagramSessionConfig;

public class DefaultDatagramSessionConfig
extends AbstractDatagramSessionConfig {
    private static final boolean DEFAULT_BROADCAST = false;
    private static final boolean DEFAULT_REUSE_ADDRESS = false;
    private static final int DEFAULT_RECEIVE_BUFFER_SIZE = -1;
    private static final int DEFAULT_SEND_BUFFER_SIZE = -1;
    private static final int DEFAULT_TRAFFIC_CLASS = 0;
    private boolean broadcast = false;
    private boolean reuseAddress = false;
    private int receiveBufferSize = -1;
    private int sendBufferSize = -1;
    private int trafficClass = 0;

    @Override
    public boolean isBroadcast() {
        return this.broadcast;
    }

    @Override
    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    @Override
    public boolean isReuseAddress() {
        return this.reuseAddress;
    }

    @Override
    public void setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
    }

    @Override
    public int getReceiveBufferSize() {
        return this.receiveBufferSize;
    }

    @Override
    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    @Override
    public int getSendBufferSize() {
        return this.sendBufferSize;
    }

    @Override
    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }

    @Override
    public int getTrafficClass() {
        return this.trafficClass;
    }

    @Override
    public void setTrafficClass(int trafficClass) {
        this.trafficClass = trafficClass;
    }

    @Override
    protected boolean isBroadcastChanged() {
        return this.broadcast;
    }

    @Override
    protected boolean isReceiveBufferSizeChanged() {
        return this.receiveBufferSize != -1;
    }

    @Override
    protected boolean isReuseAddressChanged() {
        return this.reuseAddress;
    }

    @Override
    protected boolean isSendBufferSizeChanged() {
        return this.sendBufferSize != -1;
    }

    @Override
    protected boolean isTrafficClassChanged() {
        return this.trafficClass != 0;
    }
}

