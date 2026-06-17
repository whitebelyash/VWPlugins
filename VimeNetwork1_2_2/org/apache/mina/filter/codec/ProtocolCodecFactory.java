/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public interface ProtocolCodecFactory {
    public ProtocolEncoder getEncoder(IoSession var1) throws Exception;

    public ProtocolDecoder getDecoder(IoSession var1) throws Exception;
}

