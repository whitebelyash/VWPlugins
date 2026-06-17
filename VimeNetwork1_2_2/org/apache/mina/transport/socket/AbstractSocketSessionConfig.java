/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.socket;

import org.apache.mina.core.session.AbstractIoSessionConfig;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.transport.socket.SocketSessionConfig;

public abstract class AbstractSocketSessionConfig
extends AbstractIoSessionConfig
implements SocketSessionConfig {
    @Override
    public void setAll(IoSessionConfig config) {
        super.setAll(config);
        if (!(config instanceof SocketSessionConfig)) {
            return;
        }
        if (config instanceof AbstractSocketSessionConfig) {
            AbstractSocketSessionConfig cfg = (AbstractSocketSessionConfig)config;
            if (cfg.isKeepAliveChanged()) {
                this.setKeepAlive(cfg.isKeepAlive());
            }
            if (cfg.isOobInlineChanged()) {
                this.setOobInline(cfg.isOobInline());
            }
            if (cfg.isReceiveBufferSizeChanged()) {
                this.setReceiveBufferSize(cfg.getReceiveBufferSize());
            }
            if (cfg.isReuseAddressChanged()) {
                this.setReuseAddress(cfg.isReuseAddress());
            }
            if (cfg.isSendBufferSizeChanged()) {
                this.setSendBufferSize(cfg.getSendBufferSize());
            }
            if (cfg.isSoLingerChanged()) {
                this.setSoLinger(cfg.getSoLinger());
            }
            if (cfg.isTcpNoDelayChanged()) {
                this.setTcpNoDelay(cfg.isTcpNoDelay());
            }
            if (cfg.isTrafficClassChanged() && this.getTrafficClass() != cfg.getTrafficClass()) {
                this.setTrafficClass(cfg.getTrafficClass());
            }
        } else {
            SocketSessionConfig cfg = (SocketSessionConfig)config;
            this.setKeepAlive(cfg.isKeepAlive());
            this.setOobInline(cfg.isOobInline());
            this.setReceiveBufferSize(cfg.getReceiveBufferSize());
            this.setReuseAddress(cfg.isReuseAddress());
            this.setSendBufferSize(cfg.getSendBufferSize());
            this.setSoLinger(cfg.getSoLinger());
            this.setTcpNoDelay(cfg.isTcpNoDelay());
            if (this.getTrafficClass() != cfg.getTrafficClass()) {
                this.setTrafficClass(cfg.getTrafficClass());
            }
        }
    }

    protected boolean isKeepAliveChanged() {
        return true;
    }

    protected boolean isOobInlineChanged() {
        return true;
    }

    protected boolean isReceiveBufferSizeChanged() {
        return true;
    }

    protected boolean isReuseAddressChanged() {
        return true;
    }

    protected boolean isSendBufferSizeChanged() {
        return true;
    }

    protected boolean isSoLingerChanged() {
        return true;
    }

    protected boolean isTcpNoDelayChanged() {
        return true;
    }

    protected boolean isTrafficClassChanged() {
        return true;
    }
}

