/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.demux;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;

public abstract class MessageDecoderAdapter
implements MessageDecoder {
    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
    }
}

