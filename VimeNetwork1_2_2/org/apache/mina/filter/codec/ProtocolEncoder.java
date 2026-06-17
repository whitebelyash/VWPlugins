/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public interface ProtocolEncoder {
    public void encode(IoSession var1, Object var2, ProtocolEncoderOutput var3) throws Exception;

    public void dispose(IoSession var1) throws Exception;
}

