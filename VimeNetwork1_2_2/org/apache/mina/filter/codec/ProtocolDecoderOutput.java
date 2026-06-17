/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.session.IoSession;

public interface ProtocolDecoderOutput {
    public void write(Object var1);

    public void flush(IoFilter.NextFilter var1, IoSession var2);
}

