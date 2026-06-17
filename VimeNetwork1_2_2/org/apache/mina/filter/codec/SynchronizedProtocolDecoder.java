/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class SynchronizedProtocolDecoder
implements ProtocolDecoder {
    private final ProtocolDecoder decoder;

    public SynchronizedProtocolDecoder(ProtocolDecoder decoder) {
        if (decoder == null) {
            throw new IllegalArgumentException("decoder");
        }
        this.decoder = decoder;
    }

    public ProtocolDecoder getDecoder() {
        return this.decoder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        ProtocolDecoder protocolDecoder = this.decoder;
        synchronized (protocolDecoder) {
            this.decoder.decode(session, in, out);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
        ProtocolDecoder protocolDecoder = this.decoder;
        synchronized (protocolDecoder) {
            this.decoder.finishDecode(session, out);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void dispose(IoSession session) throws Exception {
        ProtocolDecoder protocolDecoder = this.decoder;
        synchronized (protocolDecoder) {
            this.decoder.dispose(session);
        }
    }
}

