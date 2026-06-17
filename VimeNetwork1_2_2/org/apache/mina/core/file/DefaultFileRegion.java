/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.file;

import java.io.IOException;
import java.nio.channels.FileChannel;
import org.apache.mina.core.file.FileRegion;

public class DefaultFileRegion
implements FileRegion {
    private final FileChannel channel;
    private final long originalPosition;
    private long position;
    private long remainingBytes;

    public DefaultFileRegion(FileChannel channel) throws IOException {
        this(channel, 0L, channel.size());
    }

    public DefaultFileRegion(FileChannel channel, long position, long remainingBytes) {
        if (channel == null) {
            throw new IllegalArgumentException("channel can not be null");
        }
        if (position < 0L) {
            throw new IllegalArgumentException("position may not be less than 0");
        }
        if (remainingBytes < 0L) {
            throw new IllegalArgumentException("remainingBytes may not be less than 0");
        }
        this.channel = channel;
        this.originalPosition = position;
        this.position = position;
        this.remainingBytes = remainingBytes;
    }

    @Override
    public long getWrittenBytes() {
        return this.position - this.originalPosition;
    }

    @Override
    public long getRemainingBytes() {
        return this.remainingBytes;
    }

    @Override
    public FileChannel getFileChannel() {
        return this.channel;
    }

    @Override
    public long getPosition() {
        return this.position;
    }

    @Override
    public void update(long value) {
        this.position += value;
        this.remainingBytes -= value;
    }

    @Override
    public String getFilename() {
        return null;
    }
}

