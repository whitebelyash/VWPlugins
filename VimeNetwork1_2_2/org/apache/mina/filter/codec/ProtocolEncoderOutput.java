/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec;

import org.apache.mina.core.future.WriteFuture;

public interface ProtocolEncoderOutput {
    public void write(Object var1);

    public void mergeAll();

    public WriteFuture flush();
}

