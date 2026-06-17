/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.demux;

import org.apache.mina.filter.codec.demux.MessageDecoder;

public interface MessageDecoderFactory {
    public MessageDecoder getDecoder() throws Exception;
}

