/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.statemachine;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public interface DecodingState {
    public DecodingState decode(IoBuffer var1, ProtocolDecoderOutput var2) throws Exception;

    public DecodingState finishDecode(ProtocolDecoderOutput var1) throws Exception;
}

