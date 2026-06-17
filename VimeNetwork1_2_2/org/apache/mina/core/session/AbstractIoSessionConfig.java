/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.session;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSessionConfig;

public abstract class AbstractIoSessionConfig
implements IoSessionConfig {
    private int minReadBufferSize = 64;
    private int readBufferSize = 2048;
    private int maxReadBufferSize = 65536;
    private int idleTimeForRead;
    private int idleTimeForWrite;
    private int idleTimeForBoth;
    private int writeTimeout = 60;
    private boolean useReadOperation;
    private int throughputCalculationInterval = 3;

    protected AbstractIoSessionConfig() {
    }

    @Override
    public void setAll(IoSessionConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config");
        }
        this.setReadBufferSize(config.getReadBufferSize());
        this.setMinReadBufferSize(config.getMinReadBufferSize());
        this.setMaxReadBufferSize(config.getMaxReadBufferSize());
        this.setIdleTime(IdleStatus.BOTH_IDLE, config.getIdleTime(IdleStatus.BOTH_IDLE));
        this.setIdleTime(IdleStatus.READER_IDLE, config.getIdleTime(IdleStatus.READER_IDLE));
        this.setIdleTime(IdleStatus.WRITER_IDLE, config.getIdleTime(IdleStatus.WRITER_IDLE));
        this.setWriteTimeout(config.getWriteTimeout());
        this.setUseReadOperation(config.isUseReadOperation());
        this.setThroughputCalculationInterval(config.getThroughputCalculationInterval());
    }

    @Override
    public int getReadBufferSize() {
        return this.readBufferSize;
    }

    @Override
    public void setReadBufferSize(int readBufferSize) {
        if (readBufferSize <= 0) {
            throw new IllegalArgumentException("readBufferSize: " + readBufferSize + " (expected: 1+)");
        }
        this.readBufferSize = readBufferSize;
    }

    @Override
    public int getMinReadBufferSize() {
        return this.minReadBufferSize;
    }

    @Override
    public void setMinReadBufferSize(int minReadBufferSize) {
        if (minReadBufferSize <= 0) {
            throw new IllegalArgumentException("minReadBufferSize: " + minReadBufferSize + " (expected: 1+)");
        }
        if (minReadBufferSize > this.maxReadBufferSize) {
            throw new IllegalArgumentException("minReadBufferSize: " + minReadBufferSize + " (expected: smaller than " + this.maxReadBufferSize + ')');
        }
        this.minReadBufferSize = minReadBufferSize;
    }

    @Override
    public int getMaxReadBufferSize() {
        return this.maxReadBufferSize;
    }

    @Override
    public void setMaxReadBufferSize(int maxReadBufferSize) {
        if (maxReadBufferSize <= 0) {
            throw new IllegalArgumentException("maxReadBufferSize: " + maxReadBufferSize + " (expected: 1+)");
        }
        if (maxReadBufferSize < this.minReadBufferSize) {
            throw new IllegalArgumentException("maxReadBufferSize: " + maxReadBufferSize + " (expected: greater than " + this.minReadBufferSize + ')');
        }
        this.maxReadBufferSize = maxReadBufferSize;
    }

    @Override
    public int getIdleTime(IdleStatus status) {
        if (status == IdleStatus.BOTH_IDLE) {
            return this.idleTimeForBoth;
        }
        if (status == IdleStatus.READER_IDLE) {
            return this.idleTimeForRead;
        }
        if (status == IdleStatus.WRITER_IDLE) {
            return this.idleTimeForWrite;
        }
        throw new IllegalArgumentException("Unknown idle status: " + status);
    }

    @Override
    public long getIdleTimeInMillis(IdleStatus status) {
        return (long)this.getIdleTime(status) * 1000L;
    }

    @Override
    public void setIdleTime(IdleStatus status, int idleTime) {
        if (idleTime < 0) {
            throw new IllegalArgumentException("Illegal idle time: " + idleTime);
        }
        if (status == IdleStatus.BOTH_IDLE) {
            this.idleTimeForBoth = idleTime;
        } else if (status == IdleStatus.READER_IDLE) {
            this.idleTimeForRead = idleTime;
        } else if (status == IdleStatus.WRITER_IDLE) {
            this.idleTimeForWrite = idleTime;
        } else {
            throw new IllegalArgumentException("Unknown idle status: " + status);
        }
    }

    @Override
    public final int getBothIdleTime() {
        return this.getIdleTime(IdleStatus.BOTH_IDLE);
    }

    @Override
    public final long getBothIdleTimeInMillis() {
        return this.getIdleTimeInMillis(IdleStatus.BOTH_IDLE);
    }

    @Override
    public final int getReaderIdleTime() {
        return this.getIdleTime(IdleStatus.READER_IDLE);
    }

    @Override
    public final long getReaderIdleTimeInMillis() {
        return this.getIdleTimeInMillis(IdleStatus.READER_IDLE);
    }

    @Override
    public final int getWriterIdleTime() {
        return this.getIdleTime(IdleStatus.WRITER_IDLE);
    }

    @Override
    public final long getWriterIdleTimeInMillis() {
        return this.getIdleTimeInMillis(IdleStatus.WRITER_IDLE);
    }

    @Override
    public void setBothIdleTime(int idleTime) {
        this.setIdleTime(IdleStatus.BOTH_IDLE, idleTime);
    }

    @Override
    public void setReaderIdleTime(int idleTime) {
        this.setIdleTime(IdleStatus.READER_IDLE, idleTime);
    }

    @Override
    public void setWriterIdleTime(int idleTime) {
        this.setIdleTime(IdleStatus.WRITER_IDLE, idleTime);
    }

    @Override
    public int getWriteTimeout() {
        return this.writeTimeout;
    }

    @Override
    public long getWriteTimeoutInMillis() {
        return (long)this.writeTimeout * 1000L;
    }

    @Override
    public void setWriteTimeout(int writeTimeout) {
        if (writeTimeout < 0) {
            throw new IllegalArgumentException("Illegal write timeout: " + writeTimeout);
        }
        this.writeTimeout = writeTimeout;
    }

    @Override
    public boolean isUseReadOperation() {
        return this.useReadOperation;
    }

    @Override
    public void setUseReadOperation(boolean useReadOperation) {
        this.useReadOperation = useReadOperation;
    }

    @Override
    public int getThroughputCalculationInterval() {
        return this.throughputCalculationInterval;
    }

    @Override
    public void setThroughputCalculationInterval(int throughputCalculationInterval) {
        if (throughputCalculationInterval < 0) {
            throw new IllegalArgumentException("throughputCalculationInterval: " + throughputCalculationInterval);
        }
        this.throughputCalculationInterval = throughputCalculationInterval;
    }

    @Override
    public long getThroughputCalculationIntervalInMillis() {
        return (long)this.throughputCalculationInterval * 1000L;
    }
}

