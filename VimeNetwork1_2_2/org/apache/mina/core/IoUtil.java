/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

public final class IoUtil {
    private static final IoSession[] EMPTY_SESSIONS = new IoSession[0];

    public static List<WriteFuture> broadcast(Object message, Collection<IoSession> sessions) {
        ArrayList<WriteFuture> answer = new ArrayList<WriteFuture>(sessions.size());
        IoUtil.broadcast(message, sessions.iterator(), answer);
        return answer;
    }

    public static List<WriteFuture> broadcast(Object message, Iterable<IoSession> sessions) {
        ArrayList<WriteFuture> answer = new ArrayList<WriteFuture>();
        IoUtil.broadcast(message, sessions.iterator(), answer);
        return answer;
    }

    public static List<WriteFuture> broadcast(Object message, Iterator<IoSession> sessions) {
        ArrayList<WriteFuture> answer = new ArrayList<WriteFuture>();
        IoUtil.broadcast(message, sessions, answer);
        return answer;
    }

    public static List<WriteFuture> broadcast(Object message, IoSession ... sessions) {
        if (sessions == null) {
            sessions = EMPTY_SESSIONS;
        }
        ArrayList<WriteFuture> answer = new ArrayList<WriteFuture>(sessions.length);
        if (message instanceof IoBuffer) {
            for (IoSession s : sessions) {
                answer.add(s.write(((IoBuffer)message).duplicate()));
            }
        } else {
            for (IoSession s : sessions) {
                answer.add(s.write(message));
            }
        }
        return answer;
    }

    private static void broadcast(Object message, Iterator<IoSession> sessions, Collection<WriteFuture> answer) {
        if (message instanceof IoBuffer) {
            while (sessions.hasNext()) {
                IoSession s = sessions.next();
                answer.add(s.write(((IoBuffer)message).duplicate()));
            }
        } else {
            while (sessions.hasNext()) {
                IoSession s = sessions.next();
                answer.add(s.write(message));
            }
        }
    }

    public static void await(Iterable<? extends IoFuture> futures) throws InterruptedException {
        for (IoFuture ioFuture : futures) {
            ioFuture.await();
        }
    }

    public static void awaitUninterruptably(Iterable<? extends IoFuture> futures) {
        for (IoFuture ioFuture : futures) {
            ioFuture.awaitUninterruptibly();
        }
    }

    public static boolean await(Iterable<? extends IoFuture> futures, long timeout, TimeUnit unit) throws InterruptedException {
        return IoUtil.await(futures, unit.toMillis(timeout));
    }

    public static boolean await(Iterable<? extends IoFuture> futures, long timeoutMillis) throws InterruptedException {
        return IoUtil.await0(futures, timeoutMillis, true);
    }

    public static boolean awaitUninterruptibly(Iterable<? extends IoFuture> futures, long timeout, TimeUnit unit) {
        return IoUtil.awaitUninterruptibly(futures, unit.toMillis(timeout));
    }

    public static boolean awaitUninterruptibly(Iterable<? extends IoFuture> futures, long timeoutMillis) {
        try {
            return IoUtil.await0(futures, timeoutMillis, false);
        }
        catch (InterruptedException e) {
            throw new InternalError();
        }
    }

    private static boolean await0(Iterable<? extends IoFuture> futures, long timeoutMillis, boolean interruptable) throws InterruptedException {
        long startTime = timeoutMillis <= 0L ? 0L : System.currentTimeMillis();
        long waitTime = timeoutMillis;
        boolean lastComplete = true;
        Iterator<? extends IoFuture> i = futures.iterator();
        while (i.hasNext()) {
            IoFuture f = i.next();
            do {
                lastComplete = interruptable ? f.await(waitTime) : f.awaitUninterruptibly(waitTime);
                waitTime = timeoutMillis - (System.currentTimeMillis() - startTime);
            } while (!lastComplete && waitTime > 0L && !lastComplete);
            if (waitTime > 0L) continue;
            break;
        }
        return lastComplete && !i.hasNext();
    }

    private IoUtil() {
    }
}

