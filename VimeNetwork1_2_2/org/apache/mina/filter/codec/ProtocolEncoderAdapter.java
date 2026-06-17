/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;

public abstract class ProtocolEncoderAdapter
implements ProtocolEncoder {
    @Override
    public void dispose(IoSession session) throws Exception {
    }
}

