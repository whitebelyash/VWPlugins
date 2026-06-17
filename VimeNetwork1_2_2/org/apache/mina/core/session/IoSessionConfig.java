/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.session;

import org.apache.mina.core.session.IdleStatus;

public interface IoSessionConfig {
    public int getReadBufferSize();

    public void setReadBufferSize(int var1);

    public int getMinReadBufferSize();

    public void setMinReadBufferSize(int var1);

    public int getMaxReadBufferSize();

    public void setMaxReadBufferSize(int var1);

    public int getThroughputCalculationInterval();

    public long getThroughputCalculationIntervalInMillis();

    public void setThroughputCalculationInterval(int var1);

    public int getIdleTime(IdleStatus var1);

    public long getIdleTimeInMillis(IdleStatus var1);

    public void setIdleTime(IdleStatus var1, int var2);

    public int getReaderIdleTime();

    public long getReaderIdleTimeInMillis();

    public void setReaderIdleTime(int var1);

    public int getWriterIdleTime();

    public long getWriterIdleTimeInMillis();

    public void setWriterIdleTime(int var1);

    public int getBothIdleTime();

    public long getBothIdleTimeInMillis();

    public void setBothIdleTime(int var1);

    public int getWriteTimeout();

    public long getWriteTimeoutInMillis();

    public void setWriteTimeout(int var1);

    public boolean isUseReadOperation();

    public void setUseReadOperation(boolean var1);

    public void setAll(IoSessionConfig var1);
}

