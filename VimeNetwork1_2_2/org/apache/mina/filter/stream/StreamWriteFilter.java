/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.stream;

import java.io.IOException;
import java.io.InputStream;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.stream.AbstractStreamWriteFilter;

public class StreamWriteFilter
extends AbstractStreamWriteFilter<InputStream> {
    @Override
    protected IoBuffer getNextBuffer(InputStream is) throws IOException {
        int off;
        byte[] bytes = new byte[this.getWriteBufferSize()];
        int n = 0;
        for (off = 0; off < bytes.length && (n = is.read(bytes, off, bytes.length - off)) != -1; off += n) {
        }
        if (n == -1 && off == 0) {
            return null;
        }
        IoBuffer buffer = IoBuffer.wrap(bytes, 0, off);
        return buffer;
    }

    @Override
    protected Class<InputStream> getMessageClass() {
        return InputStream.class;
    }
}

