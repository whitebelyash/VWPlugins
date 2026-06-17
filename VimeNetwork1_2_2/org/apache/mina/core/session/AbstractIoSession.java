/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.DefaultFileRegion;
import org.apache.mina.core.file.FilenameFileRegion;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.DefaultCloseFuture;
import org.apache.mina.core.future.DefaultReadFuture;
import org.apache.mina.core.future.DefaultWriteFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionAttributeMap;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.write.DefaultWriteRequest;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;
import org.apache.mina.core.write.WriteTimeoutException;
import org.apache.mina.core.write.WriteToClosedSessionException;
import org.apache.mina.util.ExceptionMonitor;

public abstract class AbstractIoSession
implements IoSession {
    private final IoHandler handler;
    protected IoSessionConfig config;
    private final IoService service;
    private static final AttributeKey READY_READ_FUTURES_KEY = new AttributeKey(AbstractIoSession.class, "readyReadFutures");
    private static final AttributeKey WAITING_READ_FUTURES_KEY = new AttributeKey(AbstractIoSession.class, "waitingReadFutures");
    private static final IoFutureListener<CloseFuture> SCHEDULED_COUNTER_RESETTER = new IoFutureListener<CloseFuture>(){

        @Override
        public void operationComplete(CloseFuture future) {
            AbstractIoSession session = (AbstractIoSession)future.getSession();
            session.scheduledWriteBytes.set(0);
            session.scheduledWriteMessages.set(0);
            session.readBytesThroughput = 0.0;
            session.readMessagesThroughput = 0.0;
            session.writtenBytesThroughput = 0.0;
            session.writtenMessagesThroughput = 0.0;
        }
    };
    public static final WriteRequest CLOSE_REQUEST = new DefaultWriteRequest(new Object());
    public static final WriteRequest MESSAGE_SENT_REQUEST = new DefaultWriteRequest(DefaultWriteRequest.EMPTY_MESSAGE);
    private final Object lock = new Object();
    private IoSessionAttributeMap attributes;
    private WriteRequestQueue writeRequestQueue;
    private WriteRequest currentWriteRequest;
    private final long creationTime;
    private static AtomicLong idGenerator = new AtomicLong(0L);
    private long sessionId;
    private final CloseFuture closeFuture = new DefaultCloseFuture(this);
    private volatile boolean closing;
    private boolean readSuspended = false;
    private boolean writeSuspended = false;
    private final AtomicBoolean scheduledForFlush = new AtomicBoolean();
    private final AtomicInteger scheduledWriteBytes = new AtomicInteger();
    private final AtomicInteger scheduledWriteMessages = new AtomicInteger();
    private long readBytes;
    private long writtenBytes;
    private long readMessages;
    private long writtenMessages;
    private long lastReadTime;
    private long lastWriteTime;
    private long lastThroughputCalculationTime;
    private long lastReadBytes;
    private long lastWrittenBytes;
    private long lastReadMessages;
    private long lastWrittenMessages;
    private double readBytesThroughput;
    private double writtenBytesThroughput;
    private double readMessagesThroughput;
    private double writtenMessagesThroughput;
    private AtomicInteger idleCountForBoth = new AtomicInteger();
    private AtomicInteger idleCountForRead = new AtomicInteger();
    private AtomicInteger idleCountForWrite = new AtomicInteger();
    private long lastIdleTimeForBoth;
    private long lastIdleTimeForRead;
    private long lastIdleTimeForWrite;
    private boolean deferDecreaseReadBuffer = true;

    protected AbstractIoSession(IoService service) {
        long currentTime;
        this.service = service;
        this.handler = service.getHandler();
        this.creationTime = currentTime = System.currentTimeMillis();
        this.lastThroughputCalculationTime = currentTime;
        this.lastReadTime = currentTime;
        this.lastWriteTime = currentTime;
        this.lastIdleTimeForBoth = currentTime;
        this.lastIdleTimeForRead = currentTime;
        this.lastIdleTimeForWrite = currentTime;
        this.closeFuture.addListener(SCHEDULED_COUNTER_RESETTER);
        this.sessionId = idGenerator.incrementAndGet();
    }

    @Override
    public final long getId() {
        return this.sessionId;
    }

    public abstract IoProcessor getProcessor();

    @Override
    public final boolean isConnected() {
        return !this.closeFuture.isClosed();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public final boolean isClosing() {
        return this.closing || this.closeFuture.isClosed();
    }

    @Override
    public boolean isSecured() {
        return false;
    }

    @Override
    public final CloseFuture getCloseFuture() {
        return this.closeFuture;
    }

    public final boolean isScheduledForFlush() {
        return this.scheduledForFlush.get();
    }

    public final void scheduledForFlush() {
        this.scheduledForFlush.set(true);
    }

    public final void unscheduledForFlush() {
        this.scheduledForFlush.set(false);
    }

    public final boolean setScheduledForFlush(boolean schedule) {
        if (schedule) {
            return this.scheduledForFlush.compareAndSet(false, schedule);
        }
        this.scheduledForFlush.set(schedule);
        return true;
    }

    @Override
    public final CloseFuture close(boolean rightNow) {
        if (rightNow) {
            return this.closeNow();
        }
        return this.closeOnFlush();
    }

    @Override
    public final CloseFuture close() {
        return this.closeNow();
    }

    @Override
    public final CloseFuture closeOnFlush() {
        if (!this.isClosing()) {
            this.getWriteRequestQueue().offer(this, CLOSE_REQUEST);
            this.getProcessor().flush(this);
        }
        return this.closeFuture;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final CloseFuture closeNow() {
        Object object = this.lock;
        synchronized (object) {
            if (this.isClosing()) {
                return this.closeFuture;
            }
            this.closing = true;
            try {
                this.destroy();
            }
            catch (Exception e) {
                IoFilterChain filterChain = this.getFilterChain();
                filterChain.fireExceptionCaught(e);
            }
        }
        this.getFilterChain().fireFilterClose();
        return this.closeFuture;
    }

    protected void destroy() {
        if (this.writeRequestQueue != null) {
            while (!this.writeRequestQueue.isEmpty(this)) {
                WriteFuture writeFuture;
                WriteRequest writeRequest = this.writeRequestQueue.poll(this);
                if (writeRequest == null || (writeFuture = writeRequest.getFuture()) == null) continue;
                writeFuture.setWritten();
            }
        }
    }

    @Override
    public IoHandler getHandler() {
        return this.handler;
    }

    @Override
    public IoSessionConfig getConfig() {
        return this.config;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final ReadFuture read() {
        ReadFuture future;
        Queue<ReadFuture> readyReadFutures;
        if (!this.getConfig().isUseReadOperation()) {
            throw new IllegalStateException("useReadOperation is not enabled.");
        }
        Queue<ReadFuture> queue = readyReadFutures = this.getReadyReadFutures();
        synchronized (queue) {
            future = readyReadFutures.poll();
            if (future != null) {
                if (future.isClosed()) {
                    readyReadFutures.offer(future);
                }
            } else {
                future = new DefaultReadFuture(this);
                this.getWaitingReadFutures().offer(future);
            }
        }
        return future;
    }

    public final void offerReadFuture(Object message) {
        this.newReadFuture().setRead(message);
    }

    public final void offerFailedReadFuture(Throwable exception) {
        this.newReadFuture().setException(exception);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void offerClosedReadFuture() {
        Queue<ReadFuture> readyReadFutures;
        Queue<ReadFuture> queue = readyReadFutures = this.getReadyReadFutures();
        synchronized (queue) {
            this.newReadFuture().setClosed();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ReadFuture newReadFuture() {
        ReadFuture future;
        Queue<ReadFuture> readyReadFutures = this.getReadyReadFutures();
        Queue<ReadFuture> waitingReadFutures = this.getWaitingReadFutures();
        Queue<ReadFuture> queue = readyReadFutures;
        synchronized (queue) {
            future = waitingReadFutures.poll();
            if (future == null) {
                future = new DefaultReadFuture(this);
                readyReadFutures.offer(future);
            }
        }
        return future;
    }

    private Queue<ReadFuture> getReadyReadFutures() {
        Queue oldReadyReadFutures;
        Queue readyReadFutures = (ConcurrentLinkedQueue)this.getAttribute(READY_READ_FUTURES_KEY);
        if (readyReadFutures == null && (oldReadyReadFutures = (Queue)this.setAttributeIfAbsent(READY_READ_FUTURES_KEY, readyReadFutures = new ConcurrentLinkedQueue())) != null) {
            readyReadFutures = oldReadyReadFutures;
        }
        return readyReadFutures;
    }

    private Queue<ReadFuture> getWaitingReadFutures() {
        Queue oldWaitingReadyReadFutures;
        Queue waitingReadyReadFutures = (ConcurrentLinkedQueue)this.getAttribute(WAITING_READ_FUTURES_KEY);
        if (waitingReadyReadFutures == null && (oldWaitingReadyReadFutures = (Queue)this.setAttributeIfAbsent(WAITING_READ_FUTURES_KEY, waitingReadyReadFutures = new ConcurrentLinkedQueue())) != null) {
            waitingReadyReadFutures = oldWaitingReadyReadFutures;
        }
        return waitingReadyReadFutures;
    }

    @Override
    public WriteFuture write(Object message) {
        return this.write(message, null);
    }

    @Override
    public WriteFuture write(Object message, SocketAddress remoteAddress) {
        if (message == null) {
            throw new IllegalArgumentException("Trying to write a null message : not allowed");
        }
        if (!this.getTransportMetadata().isConnectionless() && remoteAddress != null) {
            throw new UnsupportedOperationException();
        }
        if (this.isClosing() || !this.isConnected()) {
            DefaultWriteFuture future = new DefaultWriteFuture(this);
            DefaultWriteRequest request = new DefaultWriteRequest(message, future, remoteAddress);
            WriteToClosedSessionException writeException = new WriteToClosedSessionException(request);
            future.setException(writeException);
            return future;
        }
        FileChannel openedFileChannel = null;
        try {
            if (message instanceof IoBuffer && !((IoBuffer)message).hasRemaining()) {
                throw new IllegalArgumentException("message is empty. Forgot to call flip()?");
            }
            if (message instanceof FileChannel) {
                FileChannel fileChannel = (FileChannel)message;
                message = new DefaultFileRegion(fileChannel, 0L, fileChannel.size());
            } else if (message instanceof File) {
                File file = (File)message;
                openedFileChannel = new FileInputStream(file).getChannel();
                message = new FilenameFileRegion(file, openedFileChannel, 0L, openedFileChannel.size());
            }
        }
        catch (IOException e) {
            ExceptionMonitor.getInstance().exceptionCaught(e);
            return DefaultWriteFuture.newNotWrittenFuture(this, e);
        }
        DefaultWriteFuture writeFuture = new DefaultWriteFuture(this);
        DefaultWriteRequest writeRequest = new DefaultWriteRequest(message, writeFuture, remoteAddress);
        IoFilterChain filterChain = this.getFilterChain();
        filterChain.fireFilterWrite(writeRequest);
        if (openedFileChannel != null) {
            final FileChannel finalChannel = openedFileChannel;
            writeFuture.addListener(new IoFutureListener<WriteFuture>(){

                @Override
                public void operationComplete(WriteFuture future) {
                    try {
                        finalChannel.close();
                    }
                    catch (IOException e) {
                        ExceptionMonitor.getInstance().exceptionCaught(e);
                    }
                }
            });
        }
        return writeFuture;
    }

    @Override
    public final Object getAttachment() {
        return this.getAttribute("");
    }

    @Override
    public final Object setAttachment(Object attachment) {
        return this.setAttribute("", attachment);
    }

    @Override
    public final Object getAttribute(Object key) {
        return this.getAttribute(key, null);
    }

    @Override
    public final Object getAttribute(Object key, Object defaultValue) {
        return this.attributes.getAttribute(this, key, defaultValue);
    }

    @Override
    public final Object setAttribute(Object key, Object value) {
        return this.attributes.setAttribute(this, key, value);
    }

    @Override
    public final Object setAttribute(Object key) {
        return this.setAttribute(key, Boolean.TRUE);
    }

    @Override
    public final Object setAttributeIfAbsent(Object key, Object value) {
        return this.attributes.setAttributeIfAbsent(this, key, value);
    }

    @Override
    public final Object setAttributeIfAbsent(Object key) {
        return this.setAttributeIfAbsent(key, Boolean.TRUE);
    }

    @Override
    public final Object removeAttribute(Object key) {
        return this.attributes.removeAttribute(this, key);
    }

    @Override
    public final boolean removeAttribute(Object key, Object value) {
        return this.attributes.removeAttribute(this, key, value);
    }

    @Override
    public final boolean replaceAttribute(Object key, Object oldValue, Object newValue) {
        return this.attributes.replaceAttribute(this, key, oldValue, newValue);
    }

    @Override
    public final boolean containsAttribute(Object key) {
        return this.attributes.containsAttribute(this, key);
    }

    @Override
    public final Set<Object> getAttributeKeys() {
        return this.attributes.getAttributeKeys(this);
    }

    public final IoSessionAttributeMap getAttributeMap() {
        return this.attributes;
    }

    public final void setAttributeMap(IoSessionAttributeMap attributes) {
        this.attributes = attributes;
    }

    public final void setWriteRequestQueue(WriteRequestQueue writeRequestQueue) {
        this.writeRequestQueue = writeRequestQueue;
    }

    @Override
    public final void suspendRead() {
        this.readSuspended = true;
        if (this.isClosing() || !this.isConnected()) {
            return;
        }
        this.getProcessor().updateTrafficControl(this);
    }

    @Override
    public final void suspendWrite() {
        this.writeSuspended = true;
        if (this.isClosing() || !this.isConnected()) {
            return;
        }
        this.getProcessor().updateTrafficControl(this);
    }

    @Override
    public final void resumeRead() {
        this.readSuspended = false;
        if (this.isClosing() || !this.isConnected()) {
            return;
        }
        this.getProcessor().updateTrafficControl(this);
    }

    @Override
    public final void resumeWrite() {
        this.writeSuspended = false;
        if (this.isClosing() || !this.isConnected()) {
            return;
        }
        this.getProcessor().updateTrafficControl(this);
    }

    @Override
    public boolean isReadSuspended() {
        return this.readSuspended;
    }

    @Override
    public boolean isWriteSuspended() {
        return this.writeSuspended;
    }

    @Override
    public final long getReadBytes() {
        return this.readBytes;
    }

    @Override
    public final long getWrittenBytes() {
        return this.writtenBytes;
    }

    @Override
    public final long getReadMessages() {
        return this.readMessages;
    }

    @Override
    public final long getWrittenMessages() {
        return this.writtenMessages;
    }

    @Override
    public final double getReadBytesThroughput() {
        return this.readBytesThroughput;
    }

    @Override
    public final double getWrittenBytesThroughput() {
        return this.writtenBytesThroughput;
    }

    @Override
    public final double getReadMessagesThroughput() {
        return this.readMessagesThroughput;
    }

    @Override
    public final double getWrittenMessagesThroughput() {
        return this.writtenMessagesThroughput;
    }

    @Override
    public final void updateThroughput(long currentTime, boolean force) {
        int interval = (int)(currentTime - this.lastThroughputCalculationTime);
        long minInterval = this.getConfig().getThroughputCalculationIntervalInMillis();
        if (!(minInterval != 0L && (long)interval >= minInterval || force)) {
            return;
        }
        this.readBytesThroughput = (double)(this.readBytes - this.lastReadBytes) * 1000.0 / (double)interval;
        this.writtenBytesThroughput = (double)(this.writtenBytes - this.lastWrittenBytes) * 1000.0 / (double)interval;
        this.readMessagesThroughput = (double)(this.readMessages - this.lastReadMessages) * 1000.0 / (double)interval;
        this.writtenMessagesThroughput = (double)(this.writtenMessages - this.lastWrittenMessages) * 1000.0 / (double)interval;
        this.lastReadBytes = this.readBytes;
        this.lastWrittenBytes = this.writtenBytes;
        this.lastReadMessages = this.readMessages;
        this.lastWrittenMessages = this.writtenMessages;
        this.lastThroughputCalculationTime = currentTime;
    }

    @Override
    public final long getScheduledWriteBytes() {
        return this.scheduledWriteBytes.get();
    }

    @Override
    public final int getScheduledWriteMessages() {
        return this.scheduledWriteMessages.get();
    }

    protected void setScheduledWriteBytes(int byteCount) {
        this.scheduledWriteBytes.set(byteCount);
    }

    protected void setScheduledWriteMessages(int messages) {
        this.scheduledWriteMessages.set(messages);
    }

    public final void increaseReadBytes(long increment, long currentTime) {
        if (increment <= 0L) {
            return;
        }
        this.readBytes += increment;
        this.lastReadTime = currentTime;
        this.idleCountForBoth.set(0);
        this.idleCountForRead.set(0);
        if (this.getService() instanceof AbstractIoService) {
            ((AbstractIoService)this.getService()).getStatistics().increaseReadBytes(increment, currentTime);
        }
    }

    public final void increaseReadMessages(long currentTime) {
        ++this.readMessages;
        this.lastReadTime = currentTime;
        this.idleCountForBoth.set(0);
        this.idleCountForRead.set(0);
        if (this.getService() instanceof AbstractIoService) {
            ((AbstractIoService)this.getService()).getStatistics().increaseReadMessages(currentTime);
        }
    }

    public final void increaseWrittenBytes(int increment, long currentTime) {
        if (increment <= 0) {
            return;
        }
        this.writtenBytes += (long)increment;
        this.lastWriteTime = currentTime;
        this.idleCountForBoth.set(0);
        this.idleCountForWrite.set(0);
        if (this.getService() instanceof AbstractIoService) {
            ((AbstractIoService)this.getService()).getStatistics().increaseWrittenBytes(increment, currentTime);
        }
        this.increaseScheduledWriteBytes(-increment);
    }

    public final void increaseWrittenMessages(WriteRequest request, long currentTime) {
        IoBuffer b;
        Object message = request.getMessage();
        if (message instanceof IoBuffer && (b = (IoBuffer)message).hasRemaining()) {
            return;
        }
        ++this.writtenMessages;
        this.lastWriteTime = currentTime;
        if (this.getService() instanceof AbstractIoService) {
            ((AbstractIoService)this.getService()).getStatistics().increaseWrittenMessages(currentTime);
        }
        this.decreaseScheduledWriteMessages();
    }

    public final void increaseScheduledWriteBytes(int increment) {
        this.scheduledWriteBytes.addAndGet(increment);
        if (this.getService() instanceof AbstractIoService) {
            ((AbstractIoService)this.getService()).getStatistics().increaseScheduledWriteBytes(increment);
        }
    }

    public final void increaseScheduledWriteMessages() {
        this.scheduledWriteMessages.incrementAndGet();
        if (this.getService() instanceof AbstractIoService) {
            ((AbstractIoService)this.getService()).getStatistics().increaseScheduledWriteMessages();
        }
    }

    private void decreaseScheduledWriteMessages() {
        this.scheduledWriteMessages.decrementAndGet();
        if (this.getService() instanceof AbstractIoService) {
            ((AbstractIoService)this.getService()).getStatistics().decreaseScheduledWriteMessages();
        }
    }

    public final void decreaseScheduledBytesAndMessages(WriteRequest request) {
        Object message = request.getMessage();
        if (message instanceof IoBuffer) {
            IoBuffer b = (IoBuffer)message;
            if (b.hasRemaining()) {
                this.increaseScheduledWriteBytes(-((IoBuffer)message).remaining());
            } else {
                this.decreaseScheduledWriteMessages();
            }
        } else {
            this.decreaseScheduledWriteMessages();
        }
    }

    @Override
    public final WriteRequestQueue getWriteRequestQueue() {
        if (this.writeRequestQueue == null) {
            throw new IllegalStateException();
        }
        return this.writeRequestQueue;
    }

    @Override
    public final WriteRequest getCurrentWriteRequest() {
        return this.currentWriteRequest;
    }

    @Override
    public final Object getCurrentWriteMessage() {
        WriteRequest req = this.getCurrentWriteRequest();
        if (req == null) {
            return null;
        }
        return req.getMessage();
    }

    @Override
    public final void setCurrentWriteRequest(WriteRequest currentWriteRequest) {
        this.currentWriteRequest = currentWriteRequest;
    }

    public final void increaseReadBufferSize() {
        int newReadBufferSize = this.getConfig().getReadBufferSize() << 1;
        if (newReadBufferSize <= this.getConfig().getMaxReadBufferSize()) {
            this.getConfig().setReadBufferSize(newReadBufferSize);
        } else {
            this.getConfig().setReadBufferSize(this.getConfig().getMaxReadBufferSize());
        }
        this.deferDecreaseReadBuffer = true;
    }

    public final void decreaseReadBufferSize() {
        if (this.deferDecreaseReadBuffer) {
            this.deferDecreaseReadBuffer = false;
            return;
        }
        if (this.getConfig().getReadBufferSize() > this.getConfig().getMinReadBufferSize()) {
            this.getConfig().setReadBufferSize(this.getConfig().getReadBufferSize() >>> 1);
        }
        this.deferDecreaseReadBuffer = true;
    }

    @Override
    public final long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public final long getLastIoTime() {
        return Math.max(this.lastReadTime, this.lastWriteTime);
    }

    @Override
    public final long getLastReadTime() {
        return this.lastReadTime;
    }

    @Override
    public final long getLastWriteTime() {
        return this.lastWriteTime;
    }

    @Override
    public final boolean isIdle(IdleStatus status) {
        if (status == IdleStatus.BOTH_IDLE) {
            return this.idleCountForBoth.get() > 0;
        }
        if (status == IdleStatus.READER_IDLE) {
            return this.idleCountForRead.get() > 0;
        }
        if (status == IdleStatus.WRITER_IDLE) {
            return this.idleCountForWrite.get() > 0;
        }
        throw new IllegalArgumentException("Unknown idle status: " + status);
    }

    @Override
    public final boolean isBothIdle() {
        return this.isIdle(IdleStatus.BOTH_IDLE);
    }

    @Override
    public final boolean isReaderIdle() {
        return this.isIdle(IdleStatus.READER_IDLE);
    }

    @Override
    public final boolean isWriterIdle() {
        return this.isIdle(IdleStatus.WRITER_IDLE);
    }

    @Override
    public final int getIdleCount(IdleStatus status) {
        if (this.getConfig().getIdleTime(status) == 0) {
            if (status == IdleStatus.BOTH_IDLE) {
                this.idleCountForBoth.set(0);
            }
            if (status == IdleStatus.READER_IDLE) {
                this.idleCountForRead.set(0);
            }
            if (status == IdleStatus.WRITER_IDLE) {
                this.idleCountForWrite.set(0);
            }
        }
        if (status == IdleStatus.BOTH_IDLE) {
            return this.idleCountForBoth.get();
        }
        if (status == IdleStatus.READER_IDLE) {
            return this.idleCountForRead.get();
        }
        if (status == IdleStatus.WRITER_IDLE) {
            return this.idleCountForWrite.get();
        }
        throw new IllegalArgumentException("Unknown idle status: " + status);
    }

    @Override
    public final long getLastIdleTime(IdleStatus status) {
        if (status == IdleStatus.BOTH_IDLE) {
            return this.lastIdleTimeForBoth;
        }
        if (status == IdleStatus.READER_IDLE) {
            return this.lastIdleTimeForRead;
        }
        if (status == IdleStatus.WRITER_IDLE) {
            return this.lastIdleTimeForWrite;
        }
        throw new IllegalArgumentException("Unknown idle status: " + status);
    }

    public final void increaseIdleCount(IdleStatus status, long currentTime) {
        if (status == IdleStatus.BOTH_IDLE) {
            this.idleCountForBoth.incrementAndGet();
            this.lastIdleTimeForBoth = currentTime;
        } else if (status == IdleStatus.READER_IDLE) {
            this.idleCountForRead.incrementAndGet();
            this.lastIdleTimeForRead = currentTime;
        } else if (status == IdleStatus.WRITER_IDLE) {
            this.idleCountForWrite.incrementAndGet();
            this.lastIdleTimeForWrite = currentTime;
        } else {
            throw new IllegalArgumentException("Unknown idle status: " + status);
        }
    }

    @Override
    public final int getBothIdleCount() {
        return this.getIdleCount(IdleStatus.BOTH_IDLE);
    }

    @Override
    public final long getLastBothIdleTime() {
        return this.getLastIdleTime(IdleStatus.BOTH_IDLE);
    }

    @Override
    public final long getLastReaderIdleTime() {
        return this.getLastIdleTime(IdleStatus.READER_IDLE);
    }

    @Override
    public final long getLastWriterIdleTime() {
        return this.getLastIdleTime(IdleStatus.WRITER_IDLE);
    }

    @Override
    public final int getReaderIdleCount() {
        return this.getIdleCount(IdleStatus.READER_IDLE);
    }

    @Override
    public final int getWriterIdleCount() {
        return this.getIdleCount(IdleStatus.WRITER_IDLE);
    }

    @Override
    public SocketAddress getServiceAddress() {
        IoService service = this.getService();
        if (service instanceof IoAcceptor) {
            return ((IoAcceptor)service).getLocalAddress();
        }
        return this.getRemoteAddress();
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public final boolean equals(Object o) {
        return super.equals(o);
    }

    public String toString() {
        if (this.isConnected() || this.isClosing()) {
            String remote = null;
            String local = null;
            try {
                remote = String.valueOf(this.getRemoteAddress());
            }
            catch (Exception e) {
                remote = "Cannot get the remote address informations: " + e.getMessage();
            }
            try {
                local = String.valueOf(this.getLocalAddress());
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (this.getService() instanceof IoAcceptor) {
                return "(" + this.getIdAsString() + ": " + this.getServiceName() + ", server, " + remote + " => " + local + ')';
            }
            return "(" + this.getIdAsString() + ": " + this.getServiceName() + ", client, " + local + " => " + remote + ')';
        }
        return "(" + this.getIdAsString() + ") Session disconnected ...";
    }

    private String getIdAsString() {
        String id = Long.toHexString(this.getId()).toUpperCase();
        if (id.length() <= 8) {
            return "0x00000000".substring(0, 10 - id.length()) + id;
        }
        return "0x" + id;
    }

    private String getServiceName() {
        TransportMetadata tm = this.getTransportMetadata();
        if (tm == null) {
            return "null";
        }
        return tm.getProviderName() + ' ' + tm.getName();
    }

    @Override
    public IoService getService() {
        return this.service;
    }

    public static void notifyIdleness(Iterator<? extends IoSession> sessions, long currentTime) {
        while (sessions.hasNext()) {
            IoSession session = sessions.next();
            if (session.getCloseFuture().isClosed()) continue;
            AbstractIoSession.notifyIdleSession(session, currentTime);
        }
    }

    public static void notifyIdleSession(IoSession session, long currentTime) {
        AbstractIoSession.notifyIdleSession0(session, currentTime, session.getConfig().getIdleTimeInMillis(IdleStatus.BOTH_IDLE), IdleStatus.BOTH_IDLE, Math.max(session.getLastIoTime(), session.getLastIdleTime(IdleStatus.BOTH_IDLE)));
        AbstractIoSession.notifyIdleSession0(session, currentTime, session.getConfig().getIdleTimeInMillis(IdleStatus.READER_IDLE), IdleStatus.READER_IDLE, Math.max(session.getLastReadTime(), session.getLastIdleTime(IdleStatus.READER_IDLE)));
        AbstractIoSession.notifyIdleSession0(session, currentTime, session.getConfig().getIdleTimeInMillis(IdleStatus.WRITER_IDLE), IdleStatus.WRITER_IDLE, Math.max(session.getLastWriteTime(), session.getLastIdleTime(IdleStatus.WRITER_IDLE)));
        AbstractIoSession.notifyWriteTimeout(session, currentTime);
    }

    private static void notifyIdleSession0(IoSession session, long currentTime, long idleTime, IdleStatus status, long lastIoTime) {
        if (idleTime > 0L && lastIoTime != 0L && currentTime - lastIoTime >= idleTime) {
            session.getFilterChain().fireSessionIdle(status);
        }
    }

    private static void notifyWriteTimeout(IoSession session, long currentTime) {
        WriteRequest request;
        long writeTimeout = session.getConfig().getWriteTimeoutInMillis();
        if (writeTimeout > 0L && currentTime - session.getLastWriteTime() >= writeTimeout && !session.getWriteRequestQueue().isEmpty(session) && (request = session.getCurrentWriteRequest()) != null) {
            session.setCurrentWriteRequest(null);
            WriteTimeoutException cause = new WriteTimeoutException(request);
            request.getFuture().setException(cause);
            session.getFilterChain().fireExceptionCaught(cause);
            session.closeNow();
        }
    }
}

