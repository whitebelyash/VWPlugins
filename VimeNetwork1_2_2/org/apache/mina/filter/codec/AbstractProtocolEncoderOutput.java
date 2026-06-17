/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public abstract class AbstractProtocolEncoderOutput
implements ProtocolEncoderOutput {
    private final Queue<Object> messageQueue = new ConcurrentLinkedQueue<Object>();
    private boolean buffersOnly = true;

    public Queue<Object> getMessageQueue() {
        return this.messageQueue;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void write(Object encodedMessage) {
        if (encodedMessage instanceof IoBuffer) {
            IoBuffer buf = (IoBuffer)encodedMessage;
            if (!buf.hasRemaining()) throw new IllegalArgumentException("buf is empty. Forgot to call flip()?");
            this.messageQueue.offer(buf);
            return;
        } else {
            this.messageQueue.offer(encodedMessage);
            this.buffersOnly = false;
        }
    }

    @Override
    public void mergeAll() {
        IoBuffer ioBuffer;
        if (!this.buffersOnly) {
            throw new IllegalStateException("the encoded message list contains a non-buffer.");
        }
        int size = this.messageQueue.size();
        if (size < 2) {
            return;
        }
        int sum = 0;
        for (Object e : this.messageQueue) {
            sum += ((IoBuffer)e).remaining();
        }
        IoBuffer newBuf = IoBuffer.allocate(sum);
        while ((ioBuffer = (IoBuffer)this.messageQueue.poll()) != null) {
            newBuf.put(ioBuffer);
        }
        newBuf.flip();
        this.messageQueue.add(newBuf);
    }
}

