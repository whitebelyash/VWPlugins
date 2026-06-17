/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class SynchronizedProtocolEncoder
implements ProtocolEncoder {
    private final ProtocolEncoder encoder;

    public SynchronizedProtocolEncoder(ProtocolEncoder encoder) {
        if (encoder == null) {
            throw new IllegalArgumentException("encoder");
        }
        this.encoder = encoder;
    }

    public ProtocolEncoder getEncoder() {
        return this.encoder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        ProtocolEncoder protocolEncoder = this.encoder;
        synchronized (protocolEncoder) {
            this.encoder.encode(session, message, out);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void dispose(IoSession session) throws Exception {
        ProtocolEncoder protocolEncoder = this.encoder;
        synchronized (protocolEncoder) {
            this.encoder.dispose(session);
        }
    }
}

