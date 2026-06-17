/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.file;

import java.nio.channels.FileChannel;

public interface FileRegion {
    public FileChannel getFileChannel();

    public long getPosition();

    public void update(long var1);

    public long getRemainingBytes();

    public long getWrittenBytes();

    public String getFilename();
}

