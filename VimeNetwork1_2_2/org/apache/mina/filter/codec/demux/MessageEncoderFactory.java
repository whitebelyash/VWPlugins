/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.demux;

import org.apache.mina.filter.codec.demux.MessageEncoder;

public interface MessageEncoderFactory<T> {
    public MessageEncoder<T> getEncoder() throws Exception;
}

