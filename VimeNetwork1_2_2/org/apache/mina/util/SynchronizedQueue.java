/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class SynchronizedQueue<E>
implements Queue<E>,
Serializable {
    private static final long serialVersionUID = -1439242290701194806L;
    private final Queue<E> q;

    public SynchronizedQueue(Queue<E> q) {
        this.q = q;
    }

    @Override
    public synchronized boolean add(E e) {
        return this.q.add(e);
    }

    @Override
    public synchronized E element() {
        return this.q.element();
    }

    @Override
    public synchronized boolean offer(E e) {
        return this.q.offer(e);
    }

    @Override
    public synchronized E peek() {
        return this.q.peek();
    }

    @Override
    public synchronized E poll() {
        return this.q.poll();
    }

    @Override
    public synchronized E remove() {
        return this.q.remove();
    }

    @Override
    public synchronized boolean addAll(Collection<? extends E> c) {
        return this.q.addAll(c);
    }

    @Override
    public synchronized void clear() {
        this.q.clear();
    }

    @Override
    public synchronized boolean contains(Object o) {
        return this.q.contains(o);
    }

    @Override
    public synchronized boolean containsAll(Collection<?> c) {
        return this.q.containsAll(c);
    }

    @Override
    public synchronized boolean isEmpty() {
        return this.q.isEmpty();
    }

    @Override
    public synchronized Iterator<E> iterator() {
        return this.q.iterator();
    }

    @Override
    public synchronized boolean remove(Object o) {
        return this.q.remove(o);
    }

    @Override
    public synchronized boolean removeAll(Collection<?> c) {
        return this.q.removeAll(c);
    }

    @Override
    public synchronized boolean retainAll(Collection<?> c) {
        return this.q.retainAll(c);
    }

    @Override
    public synchronized int size() {
        return this.q.size();
    }

    @Override
    public synchronized Object[] toArray() {
        return this.q.toArray();
    }

    @Override
    public synchronized <T> T[] toArray(T[] a) {
        return this.q.toArray(a);
    }

    @Override
    public synchronized boolean equals(Object obj) {
        return this.q.equals(obj);
    }

    @Override
    public synchronized int hashCode() {
        return this.q.hashCode();
    }

    public synchronized String toString() {
        return this.q.toString();
    }
}

