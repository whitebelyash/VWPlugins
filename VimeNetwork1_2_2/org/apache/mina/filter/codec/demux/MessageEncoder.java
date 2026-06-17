/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.demux;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public interface MessageEncoder<T> {
    public void encode(IoSession var1, T var2, ProtocolEncoderOutput var3) throws Exception;
}

