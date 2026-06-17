/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.mina.util.MapBackedSet;

public class ConcurrentHashSet<E>
extends MapBackedSet<E> {
    private static final long serialVersionUID = 8518578988740277828L;

    public ConcurrentHashSet() {
        super(new ConcurrentHashMap());
    }

    public ConcurrentHashSet(Collection<E> c) {
        super(new ConcurrentHashMap(), c);
    }

    @Override
    public boolean add(E o) {
        Boolean answer = ((ConcurrentMap)this.map).putIfAbsent(o, Boolean.TRUE);
        return answer == null;
    }
}

