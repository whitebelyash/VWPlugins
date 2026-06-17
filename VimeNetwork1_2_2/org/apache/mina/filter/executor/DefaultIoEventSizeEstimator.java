/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.executor;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoEvent;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.filter.executor.IoEventSizeEstimator;

public class DefaultIoEventSizeEstimator
implements IoEventSizeEstimator {
    private final ConcurrentMap<Class<?>, Integer> class2size = new ConcurrentHashMap();

    public DefaultIoEventSizeEstimator() {
        this.class2size.put(Boolean.TYPE, 4);
        this.class2size.put(Byte.TYPE, 1);
        this.class2size.put(Character.TYPE, 2);
        this.class2size.put(Integer.TYPE, 4);
        this.class2size.put(Short.TYPE, 2);
        this.class2size.put(Long.TYPE, 8);
        this.class2size.put(Float.TYPE, 4);
        this.class2size.put(Double.TYPE, 8);
        this.class2size.put(Void.TYPE, 0);
    }

    @Override
    public int estimateSize(IoEvent event) {
        return this.estimateSize((Object)event) + this.estimateSize(event.getParameter());
    }

    public int estimateSize(Object message) {
        if (message == null) {
            return 8;
        }
        int answer = 8 + this.estimateSize(message.getClass(), null);
        if (message instanceof IoBuffer) {
            answer += ((IoBuffer)message).remaining();
        } else if (message instanceof WriteRequest) {
            answer += this.estimateSize(((WriteRequest)message).getMessage());
        } else if (message instanceof CharSequence) {
            answer += ((CharSequence)message).length() << 1;
        } else if (message instanceof Iterable) {
            for (Object m : (Iterable)message) {
                answer += this.estimateSize(m);
            }
        }
        return DefaultIoEventSizeEstimator.align(answer);
    }

    private int estimateSize(Class<?> clazz, Set<Class<?>> visitedClasses) {
        Integer objectSize = (Integer)this.class2size.get(clazz);
        if (objectSize != null) {
            return objectSize;
        }
        if (visitedClasses != null) {
            if (visitedClasses.contains(clazz)) {
                return 0;
            }
        } else {
            visitedClasses = new HashSet();
        }
        visitedClasses.add(clazz);
        int answer = 8;
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            Field[] fields;
            for (Field f : fields = c.getDeclaredFields()) {
                if ((f.getModifiers() & 8) != 0) continue;
                answer += this.estimateSize(f.getType(), visitedClasses);
            }
        }
        visitedClasses.remove(clazz);
        answer = DefaultIoEventSizeEstimator.align(answer);
        Integer tmpAnswer = this.class2size.putIfAbsent(clazz, answer);
        if (tmpAnswer != null) {
            answer = tmpAnswer;
        }
        return answer;
    }

    private static int align(int size) {
        if (size % 8 != 0) {
            size /= 8;
            ++size;
            size *= 8;
        }
        return size;
    }
}

