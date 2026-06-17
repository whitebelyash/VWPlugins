/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.socket.nio;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.FileRegion;
import org.apache.mina.core.polling.AbstractPollingIoProcessor;
import org.apache.mina.core.session.SessionState;
import org.apache.mina.transport.socket.nio.NioSession;

public final class NioProcessor
extends AbstractPollingIoProcessor<NioSession> {
    private Selector selector;
    private SelectorProvider selectorProvider = null;

    public NioProcessor(Executor executor) {
        super(executor);
        try {
            this.selector = Selector.open();
        }
        catch (IOException e) {
            throw new RuntimeIoException("Failed to open a selector.", e);
        }
    }

    public NioProcessor(Executor executor, SelectorProvider selectorProvider) {
        super(executor);
        try {
            this.selector = selectorProvider == null ? Selector.open() : selectorProvider.openSelector();
        }
        catch (IOException e) {
            throw new RuntimeIoException("Failed to open a selector.", e);
        }
    }

    @Override
    protected void doDispose() throws Exception {
        this.selector.close();
    }

    @Override
    protected int select(long timeout) throws Exception {
        return this.selector.select(timeout);
    }

    @Override
    protected int select() throws Exception {
        return this.selector.select();
    }

    @Override
    protected boolean isSelectorEmpty() {
        return this.selector.keys().isEmpty();
    }

    @Override
    protected void wakeup() {
        this.wakeupCalled.getAndSet(true);
        this.selector.wakeup();
    }

    @Override
    protected Iterator<NioSession> allSessions() {
        return new IoSessionIterator<NioSession>(this.selector.keys());
    }

    @Override
    protected Iterator<NioSession> selectedSessions() {
        return new IoSessionIterator<NioSession>(this.selector.selectedKeys());
    }

    @Override
    protected void init(NioSession session) throws Exception {
        SelectableChannel ch = (SelectableChannel)((Object)session.getChannel());
        ch.configureBlocking(false);
        session.setSelectionKey(ch.register(this.selector, 1, session));
    }

    @Override
    protected void destroy(NioSession session) throws Exception {
        ByteChannel ch = session.getChannel();
        SelectionKey key = session.getSelectionKey();
        if (key != null) {
            key.cancel();
        }
        if (ch.isOpen()) {
            ch.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void registerNewSelector() throws IOException {
        Selector selector = this.selector;
        synchronized (selector) {
            Set<SelectionKey> keys = this.selector.keys();
            Selector newSelector = null;
            newSelector = this.selectorProvider == null ? Selector.open() : this.selectorProvider.openSelector();
            for (SelectionKey key : keys) {
                SelectableChannel ch = key.channel();
                NioSession session = (NioSession)key.attachment();
                SelectionKey newKey = ch.register(newSelector, key.interestOps(), session);
                session.setSelectionKey(newKey);
            }
            this.selector.close();
            this.selector = newSelector;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean isBrokenConnection() throws IOException {
        boolean brokenSession = false;
        Selector selector = this.selector;
        synchronized (selector) {
            Set<SelectionKey> keys = this.selector.keys();
            for (SelectionKey key : keys) {
                SelectableChannel channel = key.channel();
                if ((!(channel instanceof DatagramChannel) || ((DatagramChannel)channel).isConnected()) && (!(channel instanceof SocketChannel) || ((SocketChannel)channel).isConnected())) continue;
                key.cancel();
                brokenSession = true;
            }
        }
        return brokenSession;
    }

    @Override
    protected SessionState getState(NioSession session) {
        SelectionKey key = session.getSelectionKey();
        if (key == null) {
            return SessionState.OPENING;
        }
        if (key.isValid()) {
            return SessionState.OPENED;
        }
        return SessionState.CLOSING;
    }

    @Override
    protected boolean isReadable(NioSession session) {
        SelectionKey key = session.getSelectionKey();
        return key != null && key.isValid() && key.isReadable();
    }

    @Override
    protected boolean isWritable(NioSession session) {
        SelectionKey key = session.getSelectionKey();
        return key != null && key.isValid() && key.isWritable();
    }

    @Override
    protected boolean isInterestedInRead(NioSession session) {
        SelectionKey key = session.getSelectionKey();
        return key != null && key.isValid() && (key.interestOps() & 1) != 0;
    }

    @Override
    protected boolean isInterestedInWrite(NioSession session) {
        SelectionKey key = session.getSelectionKey();
        return key != null && key.isValid() && (key.interestOps() & 4) != 0;
    }

    @Override
    protected void setInterestedInRead(NioSession session, boolean isInterested) throws Exception {
        int oldInterestOps;
        SelectionKey key = session.getSelectionKey();
        if (key == null || !key.isValid()) {
            return;
        }
        int newInterestOps = oldInterestOps = key.interestOps();
        newInterestOps = isInterested ? (newInterestOps |= 1) : (newInterestOps &= 0xFFFFFFFE);
        if (oldInterestOps != newInterestOps) {
            key.interestOps(newInterestOps);
        }
    }

    @Override
    protected void setInterestedInWrite(NioSession session, boolean isInterested) throws Exception {
        SelectionKey key = session.getSelectionKey();
        if (key == null || !key.isValid()) {
            return;
        }
        int newInterestOps = key.interestOps();
        newInterestOps = isInterested ? (newInterestOps |= 4) : (newInterestOps &= 0xFFFFFFFB);
        key.interestOps(newInterestOps);
    }

    @Override
    protected int read(NioSession session, IoBuffer buf) throws Exception {
        ByteChannel channel = session.getChannel();
        return channel.read(buf.buf());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected int write(NioSession session, IoBuffer buf, int length) throws IOException {
        if (buf.remaining() <= length) {
            return session.getChannel().write(buf.buf());
        }
        int oldLimit = buf.limit();
        buf.limit(buf.position() + length);
        try {
            int n = session.getChannel().write(buf.buf());
            return n;
        }
        finally {
            buf.limit(oldLimit);
        }
    }

    @Override
    protected int transferFile(NioSession session, FileRegion region, int length) throws Exception {
        try {
            return (int)region.getFileChannel().transferTo(region.getPosition(), length, session.getChannel());
        }
        catch (IOException e) {
            String message = e.getMessage();
            if (message != null && message.contains("temporarily unavailable")) {
                return 0;
            }
            throw e;
        }
    }

    protected static class IoSessionIterator<NioSession>
    implements Iterator<NioSession> {
        private final Iterator<SelectionKey> iterator;

        private IoSessionIterator(Set<SelectionKey> keys) {
            this.iterator = keys.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public NioSession next() {
            SelectionKey key = this.iterator.next();
            Object nioSession = key.attachment();
            return (NioSession)nioSession;
        }

        @Override
        public void remove() {
            this.iterator.remove();
        }
    }
}

