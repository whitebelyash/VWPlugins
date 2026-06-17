/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec;

import java.util.LinkedList;
import java.util.Queue;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public abstract class AbstractProtocolDecoderOutput
implements ProtocolDecoderOutput {
    private final Queue<Object> messageQueue = new LinkedList<Object>();

    public Queue<Object> getMessageQueue() {
        return this.messageQueue;
    }

    @Override
    public void write(Object message) {
        if (message == null) {
            throw new IllegalArgumentException("message");
        }
        this.messageQueue.add(message);
    }
}

