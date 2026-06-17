/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public interface ProtocolDecoder {
    public void decode(IoSession var1, IoBuffer var2, ProtocolDecoderOutput var3) throws Exception;

    public void finishDecode(IoSession var1, ProtocolDecoderOutput var2) throws Exception;

    public void dispose(IoSession var1) throws Exception;
}

