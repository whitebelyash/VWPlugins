/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.write;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public class DefaultWriteRequest
implements WriteRequest {
    public static final byte[] EMPTY_MESSAGE = new byte[0];
    private static final WriteFuture UNUSED_FUTURE = new WriteFuture(){

        @Override
        public boolean isWritten() {
            return false;
        }

        @Override
        public void setWritten() {
        }

        @Override
        public IoSession getSession() {
            return null;
        }

        @Override
        public void join() {
        }

        @Override
        public boolean join(long timeoutInMillis) {
            return true;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public WriteFuture addListener(IoFutureListener<?> listener) {
            throw new IllegalStateException("You can't add a listener to a dummy future.");
        }

        @Override
        public WriteFuture removeListener(IoFutureListener<?> listener) {
            throw new IllegalStateException("You can't add a listener to a dummy future.");
        }

        @Override
        public WriteFuture await() throws InterruptedException {
            return this;
        }

        @Override
        public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return true;
        }

        @Override
        public boolean await(long timeoutMillis) throws InterruptedException {
            return true;
        }

        @Override
        public WriteFuture awaitUninterruptibly() {
            return this;
        }

        @Override
        public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
            return true;
        }

        @Override
        public boolean awaitUninterruptibly(long timeoutMillis) {
            return true;
        }

        @Override
        public Throwable getException() {
            return null;
        }

        @Override
        public void setException(Throwable cause) {
        }
    };
    private final Object message;
    private final WriteFuture future;
    private final SocketAddress destination;

    public DefaultWriteRequest(Object message) {
        this(message, null, null);
    }

    public DefaultWriteRequest(Object message, WriteFuture future) {
        this(message, future, null);
    }

    public DefaultWriteRequest(Object message, WriteFuture future, SocketAddress destination) {
        if (message == null) {
            throw new IllegalArgumentException("message");
        }
        if (future == null) {
            future = UNUSED_FUTURE;
        }
        this.message = message;
        this.future = future;
        this.destination = destination;
    }

    @Override
    public WriteFuture getFuture() {
        return this.future;
    }

    @Override
    public Object getMessage() {
        return this.message;
    }

    @Override
    public WriteRequest getOriginalRequest() {
        return this;
    }

    @Override
    public SocketAddress getDestination() {
        return this.destination;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WriteRequest: ");
        if (this.message.getClass().getName().equals(Object.class.getName())) {
            sb.append("CLOSE_REQUEST");
        } else if (this.getDestination() == null) {
            sb.append(this.message);
        } else {
            sb.append(this.message);
            sb.append(" => ");
            sb.append(this.getDestination());
        }
        return sb.toString();
    }

    @Override
    public boolean isEncoded() {
        return false;
    }
}

