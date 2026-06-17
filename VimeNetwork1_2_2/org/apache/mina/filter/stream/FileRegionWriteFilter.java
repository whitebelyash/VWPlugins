/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.stream;

import java.io.IOException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.FileRegion;
import org.apache.mina.filter.stream.AbstractStreamWriteFilter;

public class FileRegionWriteFilter
extends AbstractStreamWriteFilter<FileRegion> {
    @Override
    protected Class<FileRegion> getMessageClass() {
        return FileRegion.class;
    }

    @Override
    protected IoBuffer getNextBuffer(FileRegion fileRegion) throws IOException {
        if (fileRegion.getRemainingBytes() <= 0L) {
            return null;
        }
        int bufferSize = (int)Math.min((long)this.getWriteBufferSize(), fileRegion.getRemainingBytes());
        IoBuffer buffer = IoBuffer.allocate(bufferSize);
        int bytesRead = fileRegion.getFileChannel().read(buffer.buf(), fileRegion.getPosition());
        fileRegion.update(bytesRead);
        buffer.flip();
        return buffer;
    }
}

