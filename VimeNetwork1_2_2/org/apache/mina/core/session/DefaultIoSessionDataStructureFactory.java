/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.session;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionAttributeMap;
import org.apache.mina.core.session.IoSessionDataStructureFactory;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;

public class DefaultIoSessionDataStructureFactory
implements IoSessionDataStructureFactory {
    @Override
    public IoSessionAttributeMap getAttributeMap(IoSession session) throws Exception {
        return new DefaultIoSessionAttributeMap();
    }

    @Override
    public WriteRequestQueue getWriteRequestQueue(IoSession session) throws Exception {
        return new DefaultWriteRequestQueue();
    }

    private static class DefaultWriteRequestQueue
    implements WriteRequestQueue {
        private final Queue<WriteRequest> q = new ConcurrentLinkedQueue<WriteRequest>();

        @Override
        public void dispose(IoSession session) {
        }

        @Override
        public void clear(IoSession session) {
            this.q.clear();
        }

        @Override
        public boolean isEmpty(IoSession session) {
            return this.q.isEmpty();
        }

        @Override
        public void offer(IoSession session, WriteRequest writeRequest) {
            this.q.offer(writeRequest);
        }

        @Override
        public WriteRequest poll(IoSession session) {
            WriteRequest answer = this.q.poll();
            if (answer == AbstractIoSession.CLOSE_REQUEST) {
                session.closeNow();
                this.dispose(session);
                answer = null;
            }
            return answer;
        }

        public String toString() {
            return this.q.toString();
        }

        @Override
        public int size() {
            return this.q.size();
        }
    }

    private static class DefaultIoSessionAttributeMap
    implements IoSessionAttributeMap {
        private final ConcurrentHashMap<Object, Object> attributes = new ConcurrentHashMap(4);

        @Override
        public Object getAttribute(IoSession session, Object key, Object defaultValue) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }
            if (defaultValue == null) {
                return this.attributes.get(key);
            }
            Object object = this.attributes.putIfAbsent(key, defaultValue);
            if (object == null) {
                return defaultValue;
            }
            return object;
        }

        @Override
        public Object setAttribute(IoSession session, Object key, Object value) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }
            if (value == null) {
                return this.attributes.remove(key);
            }
            return this.attributes.put(key, value);
        }

        @Override
        public Object setAttributeIfAbsent(IoSession session, Object key, Object value) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }
            if (value == null) {
                return null;
            }
            return this.attributes.putIfAbsent(key, value);
        }

        @Override
        public Object removeAttribute(IoSession session, Object key) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }
            return this.attributes.remove(key);
        }

        @Override
        public boolean removeAttribute(IoSession session, Object key, Object value) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }
            if (value == null) {
                return false;
            }
            try {
                return this.attributes.remove(key, value);
            }
            catch (NullPointerException e) {
                return false;
            }
        }

        @Override
        public boolean replaceAttribute(IoSession session, Object key, Object oldValue, Object newValue) {
            try {
                return this.attributes.replace(key, oldValue, newValue);
            }
            catch (NullPointerException nullPointerException) {
                return false;
            }
        }

        @Override
        public boolean containsAttribute(IoSession session, Object key) {
            return this.attributes.containsKey(key);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<Object> getAttributeKeys(IoSession session) {
            ConcurrentHashMap<Object, Object> concurrentHashMap = this.attributes;
            synchronized (concurrentHashMap) {
                return new HashSet<Object>(this.attributes.keySet());
            }
        }

        @Override
        public void dispose(IoSession session) throws Exception {
        }
    }
}

