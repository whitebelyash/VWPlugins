/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.socket;

import org.apache.mina.core.service.IoService;
import org.apache.mina.transport.socket.AbstractSocketSessionConfig;
import org.apache.mina.transport.socket.SocketAcceptor;

public class DefaultSocketSessionConfig
extends AbstractSocketSessionConfig {
    private static final boolean DEFAULT_REUSE_ADDRESS = false;
    private static final int DEFAULT_TRAFFIC_CLASS = 0;
    private static final boolean DEFAULT_KEEP_ALIVE = false;
    private static final boolean DEFAULT_OOB_INLINE = false;
    private static final int DEFAULT_SO_LINGER = -1;
    private static final boolean DEFAULT_TCP_NO_DELAY = false;
    protected IoService parent;
    private boolean defaultReuseAddress;
    private boolean reuseAddress;
    private int receiveBufferSize = -1;
    private int sendBufferSize = -1;
    private int trafficClass = 0;
    private boolean keepAlive = false;
    private boolean oobInline = false;
    private int soLinger = -1;
    private boolean tcpNoDelay = false;

    public void init(IoService parent) {
        this.parent = parent;
        this.defaultReuseAddress = parent instanceof SocketAcceptor;
        this.reuseAddress = this.defaultReuseAddress;
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
    public boolean isKeepAlive() {
        return this.keepAlive;
    }

    @Override
    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    @Override
    public boolean isOobInline() {
        return this.oobInline;
    }

    @Override
    public void setOobInline(boolean oobInline) {
        this.oobInline = oobInline;
    }

    @Override
    public int getSoLinger() {
        return this.soLinger;
    }

    @Override
    public void setSoLinger(int soLinger) {
        this.soLinger = soLinger;
    }

    @Override
    public boolean isTcpNoDelay() {
        return this.tcpNoDelay;
    }

    @Override
    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    @Override
    protected boolean isKeepAliveChanged() {
        return this.keepAlive;
    }

    @Override
    protected boolean isOobInlineChanged() {
        return this.oobInline;
    }

    @Override
    protected boolean isReceiveBufferSizeChanged() {
        return this.receiveBufferSize != -1;
    }

    @Override
    protected boolean isReuseAddressChanged() {
        return this.reuseAddress != this.defaultReuseAddress;
    }

    @Override
    protected boolean isSendBufferSizeChanged() {
        return this.sendBufferSize != -1;
    }

    @Override
    protected boolean isSoLingerChanged() {
        return this.soLinger != -1;
    }

    @Override
    protected boolean isTcpNoDelayChanged() {
        return this.tcpNoDelay;
    }

    @Override
    protected boolean isTrafficClassChanged() {
        return this.trafficClass != 0;
    }
}

