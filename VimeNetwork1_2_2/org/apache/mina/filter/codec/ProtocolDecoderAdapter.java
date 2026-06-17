/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public abstract class ProtocolDecoderAdapter
implements ProtocolDecoder {
    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
    }

    @Override
    public void dispose(IoSession session) throws Exception {
    }
}

